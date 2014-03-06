package quickdt.PredictiveModelOptimizer;

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
    List<Parameter> parameters;
    Map<String, Object> predictiveModelConfig;
    String nameOfPredictiveModel;
    List<Instance> trainingData;
    private int countsTowardsConvergence=0;
    private int maxIterations = 3;
    private int minIterations = 2;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;

    public PredictiveModelOptimizer(String nameOfPredictiveModel, List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, CrossValidator crossValidator, List<Instance> trainingData ) {
        this.crossValidator = crossValidator;
        this.parameters = parameters;
        this.nameOfPredictiveModel = nameOfPredictiveModel;
        this.trainingData = trainingData;
        setPredictiveModelConfig();
    }

    private void setPredictiveModelConfig() {
        predictiveModelConfig = new HashMap<String, Object>();
        for (Parameter parameter : parameters)
            predictiveModelConfig.put(parameter.properties.name, parameter.properties.optimalValue);
    }

    public Map<String, Object> findOptimalParameters() {
        boolean converged = false;
        while (!converged) {
            for (Parameter parameter : parameters) {
                parameter.trialValues.setPrevious();
                findOptimalParameterValue(parameter);
            }
            converged = isConverged();
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

            if (i==0 || loss < minLoss) {
                minLoss = loss;
                parameter.trialValues.current = paramValue;
            }
        }
    }

    private boolean isConverged() {  // what will be the condition
        boolean converged = true;
        countsTowardsConvergence++;
        if (countsTowardsConvergence > minIterations)
            return false;
        else if (countsTowardsConvergence > maxIterations)
            return true;
        else {
            for (Parameter parameter : parameters)  {
                System.out.println(parameter.properties.name + " current value " + parameter.trialValues.current + " previous value " + parameter.trialValues.previous);
                System.out.println(parameter.properties.name + " current error " + parameter.trialErrors.current + " previous error " + parameter.trialErrors.previous);

                if(parameterIsConverged(parameter) == false || errorIsWithinTolerance(parameter) == false);
                    return false;

            }
        }
        return converged;
    }

    private boolean parameterIsConverged(Parameter parameter) {
        boolean converged = true;
        if (parameter.trialValues.current instanceof Number && Math.abs(((Number) parameter.trialValues.current).doubleValue() - ((Number) parameter.trialValues.previous).doubleValue()) > parameter.properties.errorTolerance)
            converged = false;
        else if (parameter.trialValues.current instanceof Boolean && !parameter.trialValues.current.equals(parameter.trialValues.previous))
            converged = false;
        else  {
            System.out.println("parameters to optimize must be numbers or booleans");
            System.exit(0);
        }
        return converged;
    }

    private boolean errorIsWithinTolerance(Parameter parameter) {
        boolean converged = true;
        if (Math.abs((Double)parameter.trialErrors.current - (Double)parameter.trialErrors.previous) > parameter.properties.errorTolerance)
            converged = false;

        return converged;
    }
}
