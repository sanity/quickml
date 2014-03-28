package quickdt.predictiveModelOptimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.*;
import quickdt.experiments.crossValidation.CrossValidator;


import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class PredictiveModelOptimizer {
    private static final  Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizer.class);

    List<ParameterToOptimize> parametersToOptimize;
    Map<String, Object> predictiveModelParamaters;
    String nameOfPredictiveModel;
    Iterable<? extends AbstractInstance> trainingData;
    private int iterations =0;
    private int maxIterations = 12;
    private int minIterations = 2;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;
    private int userMaxIterations;

    public PredictiveModelOptimizer(int userMaxIterations, CrossValidator crossValidator, List<ParameterToOptimize> parametersToOptimize, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<? extends AbstractInstance> trainingData ) {
        this.userMaxIterations = userMaxIterations;
        this.maxIterations = userMaxIterations;
        this.parametersToOptimize = parametersToOptimize;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.trainingData = trainingData;
        this.predictiveModelParamaters = predictiveModelBuilderBuilder.createPredictiveModelConfig(parametersToOptimize);
    }


    public PredictiveModelOptimizer(List<ParameterToOptimize> parametersToOptimize, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<? extends AbstractInstance> trainingData ) {
        this.userMaxIterations = maxIterations;
        this.parametersToOptimize = parametersToOptimize;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = new CrossValidator();
        this.trainingData = trainingData;
        this.predictiveModelParamaters = predictiveModelBuilderBuilder.createPredictiveModelConfig(parametersToOptimize);
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<? extends AbstractInstance> trainingData ) {
        this.userMaxIterations = maxIterations;
        this.parametersToOptimize = predictiveModelBuilderBuilder.createDefaultParametersToOptimize();
        this.crossValidator = new CrossValidator();
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.trainingData = trainingData;
        this.predictiveModelParamaters = predictiveModelBuilderBuilder.createPredictiveModelConfig(parametersToOptimize);
    }

    public Map<String, Object> findOptimalParameters() {

        findOptimalParametersIteratively();
        return predictiveModelParamaters;
    }

    private void findOptimalParametersIteratively() {
        boolean converged = false;
        while (!converged) {
            for (ParameterToOptimize parameterToOptimize : parametersToOptimize) {
                parameterToOptimize.trialValues.setPrevious();
                parameterToOptimize.trialErrors.setPrevious();

                findOptimalParameterValue(parameterToOptimize);
            }
            iterations++;

            if (iterations > 1){
                converged = isConverged();
                logger.info("\n checking convergence \n");
            }
            if(iterations >= userMaxIterations)
               break;
        }
    }

    private void findOptimalParameterValue(ParameterToOptimize parameterToOptimize){
        double loss=0, minLoss=0;
        PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder;
        double relativeError = 0;
        for (int i=0; i< parameterToOptimize.properties.range.size(); i++)  {
            Object paramValue = parameterToOptimize.properties.range.get(i);
            predictiveModelParamaters.put(parameterToOptimize.properties.name, paramValue);
            predictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelParamaters);
            loss = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);
            logger.info(String.format("parameterToOptimize: %s with value %s, has loss %f .  Other params are:", parameterToOptimize.properties.name, paramValue, loss));
            for (String key : predictiveModelParamaters.keySet())
                if (!key.equals("loss") && !key.equals(parameterToOptimize.properties.name))
                   logger.info(String.format(key + " " + predictiveModelParamaters.get(key)));

            if (parameterToOptimize.properties.isMonotonicallyConvergent)  {
                relativeError = Math.abs(loss - minLoss)/loss;//Math.abs(((Number)parameter.trialErrors.current).doubleValue() - ((Number) parameter.trialErrors.previous).doubleValue())/((Number)(parameter.trialErrors.previous)).doubleValue();
                parameterToOptimize.trialValues.current = paramValue;
                parameterToOptimize.trialErrors.current = loss;
                logger.info("relative Error" + " " + relativeError);
                if (relativeError < parameterToOptimize.properties.errorTolerance)  {
                    break;
                }
            }
            if (i==0 || loss < minLoss) {
                minLoss = loss;
                parameterToOptimize.trialValues.current = paramValue;
                parameterToOptimize.trialErrors.current = loss;
            }
        }
        predictiveModelParamaters.put("loss", parameterToOptimize.trialErrors.current);
        predictiveModelParamaters.put(parameterToOptimize.properties.name, parameterToOptimize.trialValues.current);
        logger.info(String.format("Best value for parameterToOptimize %s is %s with loss of %s", parameterToOptimize.properties.name, parameterToOptimize.trialValues.current, parameterToOptimize.trialErrors.current));
    }

    private boolean isConverged() {
        boolean converged = true;
        if (iterations < minIterations)
            return false;
        else if (iterations > maxIterations) {
            logger.info(String.format("did not converge.  Stopped because we exceeded Max Iterations %d", maxIterations));
            return true;
        }
        else {
            for (ParameterToOptimize parameterToOptimize : parametersToOptimize)  {
                logger.info(parameterToOptimize.properties.name + " current value " + parameterToOptimize.trialValues.current + " previous value " + parameterToOptimize.trialValues.previous);

                if(parameterIsConverged(parameterToOptimize) == false)
                    converged = false;

            }
        }
        return converged;
    }

    private boolean parameterIsConverged(ParameterToOptimize parameterToOptimize) {
        boolean converged = true;
        if (!(parameterToOptimize.trialValues.current instanceof Number) && !(parameterToOptimize.trialValues.current instanceof Boolean)) {
            logger.error("parametersToOptimize to optimize must be numbers or booleans");
            System.exit(0);
        }
        else if (parameterToOptimize.trialValues.current instanceof Number && Math.abs(((Number) parameterToOptimize.trialValues.current).doubleValue() - ((Number) parameterToOptimize.trialValues.previous).doubleValue()) > parameterToOptimize.properties.errorTolerance)
            converged = false;
        else if (parameterToOptimize.trialValues.current instanceof Boolean && !parameterToOptimize.trialValues.current.equals(parameterToOptimize.trialValues.previous))
            converged = false;

        return converged;
    }

    private boolean errorIsWithinTolerance(ParameterToOptimize parameterToOptimize) {
        boolean converged = true;
        double percentError = Math.abs((Double)parameterToOptimize.trialErrors.current - (Double)parameterToOptimize.trialErrors.previous)/((Double)parameterToOptimize.trialErrors.current);
        if (percentError > parameterToOptimize.properties.errorTolerance)
            converged = false;

        return converged;
    }
}
