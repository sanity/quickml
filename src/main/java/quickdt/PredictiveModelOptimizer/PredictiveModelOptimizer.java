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

    List<Parameter> parameters;
    Map<String, Object> predictiveModelConfig;
    String nameOfPredictiveModel;
    Iterable<? extends AbstractInstance> trainingData;
    private int iterations =0;
    private int maxIterations = 12;
    private int minIterations = 2;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;

    public PredictiveModelOptimizer(CrossValidator crossValidator, List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<? extends AbstractInstance> trainingData ) {
        this.parameters = parameters;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.trainingData = trainingData;
        this.predictiveModelConfig = predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }

    public PredictiveModelOptimizer(List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<? extends AbstractInstance> trainingData ) {
        this.parameters = parameters;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = new CrossValidator();
        this.trainingData = trainingData;
        this.predictiveModelConfig = predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<? extends AbstractInstance> trainingData ) {
        this.parameters = predictiveModelBuilderBuilder.createDefaultParameters();
        this.crossValidator = new CrossValidator();
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.trainingData = trainingData;
        this.predictiveModelConfig = predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }

    public Map<String, Object> findOptimalParameters() {
        boolean converged = false;
        while (!converged) {
            for (Parameter parameter : parameters) {
                parameter.trialValues.setPrevious();
                parameter.trialErrors.setPrevious();

                findOptimalParameterValue(parameter);
            }
            iterations++;

            if (iterations > 1){
                converged = isConverged();
                logger.info("\n checking convergence \n");
            }
        }
        return predictiveModelConfig;
    }

    private void findOptimalParameterValue(Parameter parameter){
        double loss=0, minLoss=0;
        PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder;
        double relativeError = 0;
        for (int i=0; i< parameter.properties.range.size(); i++)  {
            Object paramValue = parameter.properties.range.get(i);
            predictiveModelConfig.put(parameter.properties.name, paramValue);
            predictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
            loss = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);
            logger.info(String.format("Trying parameter %s with value %s, loss is %f", parameter.properties.name, paramValue, loss));
            for (String key : predictiveModelConfig.keySet())
                logger.info(String.format(key + " " + predictiveModelConfig.get(key)));

            if (parameter.properties.isMonotonicallyConvergent)  {
                relativeError = Math.abs(loss - minLoss)/loss;//Math.abs(((Number)parameter.trialErrors.current).doubleValue() - ((Number) parameter.trialErrors.previous).doubleValue())/((Number)(parameter.trialErrors.previous)).doubleValue();
                parameter.trialValues.current = paramValue;
                parameter.trialErrors.current = loss;
                logger.info("relative Error" + " " + relativeError);
                if (relativeError < parameter.properties.errorTolerance)  {
                    break;
                }
            }
            if (i==0 || loss < minLoss) {
                minLoss = loss;
                parameter.trialValues.current = paramValue;
                parameter.trialErrors.current = loss;
            }
        }
        predictiveModelConfig.put("loss", parameter.trialErrors.current);
        predictiveModelConfig.put(parameter.properties.name, parameter.trialValues.current);
        logger.info(String.format("Best value for parameter %s is %s with loss of %s", parameter.properties.name, parameter.trialValues.current, parameter.trialErrors.current));
    }

    private boolean isConverged() {
        boolean converged = true;
        if (iterations < minIterations)
            return false;
        else if (iterations > maxIterations) {
            logger.info(String.format("exceededMax Iterations %d", maxIterations));

            return true;
        }
        else {
            for (Parameter parameter : parameters)  {
                logger.info(parameter.properties.name + " current value " + parameter.trialValues.current + " previous value " + parameter.trialValues.previous);

                if(parameterIsConverged(parameter) == false)
                    converged = false;

            }
        }
        return converged;
    }

    private boolean parameterIsConverged(Parameter parameter) {
        boolean converged = true;
        if (!(parameter.trialValues.current instanceof Number) && !(parameter.trialValues.current instanceof Boolean)) {
            logger.error("parameters to optimize must be numbers or booleans");
            System.exit(0);
        }
        else if (parameter.trialValues.current instanceof Number && Math.abs(((Number) parameter.trialValues.current).doubleValue() - ((Number) parameter.trialValues.previous).doubleValue()) > parameter.properties.errorTolerance)
            converged = false;
        else if (parameter.trialValues.current instanceof Boolean && !parameter.trialValues.current.equals(parameter.trialValues.previous))
            converged = false;

        return converged;
    }

    private boolean errorIsWithinTolerance(Parameter parameter) {
        boolean converged = true;
        double percentError = Math.abs((Double)parameter.trialErrors.current - (Double)parameter.trialErrors.previous)/((Double)parameter.trialErrors.current);
        if (percentError > parameter.properties.errorTolerance)
            converged = false;

        return converged;
    }
}
