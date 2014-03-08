package quickdt.predictiveModelOptimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Instance;
import quickdt.PredictiveModel;
import quickdt.PredictiveModelBuilder;
import quickdt.experiments.crossValidation.CrossValidator;

import java.util.HashMap;
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
    List<Instance> trainingData;
    private int iterations =0;
    private int maxIterations = 6;
    private int minIterations = 2;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;

    public PredictiveModelOptimizer(String nameOfPredictiveModel, List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, CrossValidator crossValidator, List<Instance> trainingData ) {
        this.nameOfPredictiveModel = nameOfPredictiveModel;
        this.parameters = parameters;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.trainingData = trainingData;
        setPredictiveModelConfig();
    }

    private void setPredictiveModelConfig() {
        predictiveModelConfig = new HashMap<String, Object>();
        for (Parameter parameter : parameters)
            predictiveModelConfig.put(parameter.properties.name, parameter.properties.optimalValue);
        if (!predictiveModelConfig.containsKey("maxDepth"))
            predictiveModelConfig.put("maxDepth", new Integer(4));
        if (!predictiveModelConfig.containsKey("ignoreAttributeAtNodeProbability"))
            predictiveModelConfig.put("ignoreAttributeAtNodeProbability", new Double(0.7));
        if (!predictiveModelConfig.containsKey("numTrees"))
            predictiveModelConfig.put("numTrees", new Integer(4));
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
            }
        }
        return predictiveModelConfig;
    }

    private void findOptimalParameterValue(Parameter parameter){
        double loss=0, minLoss=0;
        PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder;
        for (int i=0; i< parameter.properties.range.size(); i++)  {
            Object paramValue = parameter.properties.range.get(i);
            predictiveModelConfig.put(parameter.properties.name, paramValue);
            predictiveModelBuilder = predictiveModelBuilderBuilder.build(predictiveModelConfig);
            loss = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);
            logger.info(String.format("Trying parameter %s with value %s, loss is %s", parameter.properties.name, paramValue, loss));
            if (i==0 || loss < minLoss) {
                minLoss = loss;
                parameter.trialValues.current = paramValue;
                parameter.trialErrors.current = minLoss;
            }
        }
        logger.info(String.format("Best value for parameter %s is %s with loss of %s", parameter.properties.name, parameter.trialValues.current, parameter.trialErrors.current));
    }

    private boolean isConverged() {  // what will be the condition
        boolean converged = true;
        if (iterations < minIterations)
            return false;
        else if (iterations > maxIterations)
            return true;
        else {
            for (Parameter parameter : parameters)  {
                logger.info(parameter.properties.name + " current value " + parameter.trialValues.current + " previous value " + parameter.trialValues.previous);

                if(parameterIsConverged(parameter) == false || errorIsWithinTolerance(parameter) == false);
                    converged = false;

            }
        }
        return converged;
    }

    private boolean parameterIsConverged(Parameter parameter) {
        boolean converged = true;
        if (!(parameter.trialValues.current instanceof Number) && !(parameter.trialValues.current instanceof Boolean)) {
            System.out.println("parameters to optimize must be numbers or booleans");
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

        System.out.println("current % error" + percentError);
        return converged;
    }
}
