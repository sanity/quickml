package quickdt.Optimizer;

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
public class Optimizer {
    List<ParameterInfo> parameterInfos;
    Map<String, Object> predictiveModelConfig;
    String nameOfPredictiveModel;
    List<Instance> trainingData;
    private int countsTowardsConvergence=0;
    private int maxCountsTowardsConvergence = 3;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;

    public Optimizer(String nameOfPredictiveModel, List<ParameterInfo> parameterInfos, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, CrossValidator crossValidator, List<Instance> trainingData ) {
        this.crossValidator = crossValidator;
        this.parameterInfos = parameterInfos;
        this.nameOfPredictiveModel = nameOfPredictiveModel;
        this.trainingData = trainingData;
        setPredictiveModelConfig();
    }

    private void setPredictiveModelConfig() {
        predictiveModelConfig = new HashMap<String, Object>();
        for (ParameterInfo parameterInfo : parameterInfos)
            predictiveModelConfig.put(parameterInfo.name, parameterInfo.optimalValue);
    }

    public void findOptimalParameters() {
        boolean converged = false;
        while (!converged) {
            for (ParameterInfo parameterInfo : parameterInfos) {
                parameterInfo.setValuesFromPreviousIteration();
                findOptimalParameterValue(parameterInfo);
            }
            converged = setConverged();
        }
        return;
    }

    private void findOptimalParameterValue(ParameterInfo parameterInfo){
        double loss=0, minLoss=0;
        PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder;
        for (int i=0; i<parameterInfo.range.size(); i++)  {
            Object paramValue = parameterInfo.range.get(i);
            predictiveModelConfig.put(parameterInfo.name, paramValue);
            predictiveModelBuilder = predictiveModelBuilderBuilder.build(predictiveModelConfig);
            loss = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);

            if (i==0 || loss < minLoss) {
                minLoss = loss;
                parameterInfo.optimalValue = paramValue;
            }
        }
    }

    private boolean setConverged() {  // what will be the condition
        countsTowardsConvergence++;
        if (countsTowardsConvergence > maxCountsTowardsConvergence)
            return true;
        else {
            boolean converged = true;
            for (ParameterInfo parameterInfo : parameterInfos)
                if(checkParameterConvergence(parameterInfo) == false || checkErrorConvergence(parameterInfo) == false);
                    return false;
        }
    }

    private boolean checkParameterConvergence(ParameterInfo parameterInfo) {
        boolean converged = true;
        if (parameterInfo.optimalValue instanceof Integer && Math.abs((Integer)parameterInfo.optimalValue - (Integer)parameterInfo.optimalValueFromPreviousIteration) > parameterInfo.parameterTolerance)
            converged = false;
        else if (parameterInfo.optimalValue instanceof Double && Math.abs((Double)parameterInfo.optimalValue - (Double)parameterInfo.optimalValueFromPreviousIteration) > parameterInfo.parameterTolerance)
            converged = false;
        else if (parameterInfo.optimalValue instanceof Boolean && !parameterInfo.optimalValue.equals(parameterInfo.optimalValueFromPreviousIteration));
            converged = false;

        return converged;
    }

    private boolean checkErrorConvergence(ParameterInfo parameterInfo) {
        boolean converged = true;
        if (Math.abs(parameterInfo.lossFromOptimalValue - parameterInfo.lossFromPreviousOptimalValue) > parameterInfo.errorTolerance)
            converged = false;

        return converged;
    }
}
