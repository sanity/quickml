package quickml.supervised.crossValidation.crossValLossFunctions;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/15/14.
 */
public class MultiLossFunctionWithModelConfigurations<P> implements CrossValLossFunction<P>{
    /*
    This class stores a map of loss functions to LossForModelConfigurations (which stores a model's calculated loss and the model's configuration parameters).   */

    private HashMap<String, LossWithModelConfiguration> lossesWithModelConfigurations = Maps.newHashMap();
    String primaryLossFunctionName;
    public double runningWeight;
    public Map<String, Double> runningLosses;
    Map<String, CrossValLossFunction<P>> lossFunctions;

    public MultiLossFunctionWithModelConfigurations(Map<String, CrossValLossFunction<P>> lossFunctions, String primaryLossFunctionName) {
        this.lossFunctions = lossFunctions;
        this.primaryLossFunctionName = primaryLossFunctionName;
    }

    public double getLoss(List<LabelPredictionWeight<P>> labelPredictionWeights) {
        CrossValLossFunction<P> primaryLossFunction = lossFunctions.get(primaryLossFunctionName);
        return primaryLossFunction.getLoss(labelPredictionWeights);
    }

   public void mergeInBestLossesWithConfigurations(MultiLossFunctionWithModelConfigurations<P> other){
       HashMap<String, LossWithModelConfiguration> lossesWithModelConfigurationsOther = other.getLossesWithModelConfigurations();
       for (String lossFunctionName : lossesWithModelConfigurations.keySet()) {
            if (!lossesWithModelConfigurationsOther.containsKey(lossFunctionName))
                continue;
            LossWithModelConfiguration lossWithModelConfiguration = lossesWithModelConfigurations.get(lossFunctionName);
            LossWithModelConfiguration lossWithModelConfigurationOther = lossesWithModelConfigurationsOther.get(lossFunctionName);

           double loss = lossWithModelConfiguration.getLoss();
           double lossOther = lossWithModelConfigurationOther.getLoss();

           if ( loss > lossOther) {
                lossWithModelConfiguration.setLoss(lossOther);
                lossWithModelConfiguration.setConfiguration(lossWithModelConfigurationOther.getModelConfiguration());
            }
        }
    }

    public void updateRunningLosses(List<LabelPredictionWeight<P>> labelPredictionWeights, double weightOfNewValidationSet){
        this.runningWeight += weightOfNewValidationSet; //make sure to 0 this at start of a new cross validation
        for (String lossFunctionName : lossesWithModelConfigurations.keySet()) {
            double previousWeightedLoss = runningLosses.get(lossFunctionName);
            CrossValLossFunction<P> lossFunction = lossFunctions.get(lossFunctionName);
            double weightedLossFromValidationRun = lossFunction.getLoss(labelPredictionWeights) * weightOfNewValidationSet;
            runningLosses.put(lossFunctionName, weightedLossFromValidationRun + previousWeightedLoss);
        }
    }

    public void clearRunningLossData(){
        runningWeight = 0;
        runningLosses = Maps.newHashMap();
    }

    public HashMap<String, LossWithModelConfiguration>  getAverageLossesWithConfigurations(List<LabelPredictionWeight<P>> labelPredictionWeights, double weightOfNewValidationSet){
        this.runningWeight += weightOfNewValidationSet;
        for (String lossFunctionName : lossesWithModelConfigurations.keySet()) {
            lossesWithModelConfigurations.get(lossFunctionName).setLoss(runningLosses.get(lossFunctionName)/runningWeight);
        }
        clearRunningLossData();
        return lossesWithModelConfigurations;
    }

    public HashMap<String, LossWithModelConfiguration> getLossesWithModelConfigurations() {
        return lossesWithModelConfigurations;
    }

}

class LossWithModelConfiguration {
    double loss;
    Map<String, Serializable> modelConfiguration;

    public LossWithModelConfiguration(){}

    public LossWithModelConfiguration(double loss, Map<String, Serializable> modelConfiguration) {
        this.loss = loss;
        this.modelConfiguration = modelConfiguration;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public void setConfiguration(Map<String, Serializable> modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    public double getLoss(){
        return loss;
    }
    public Map<String, Serializable> getModelConfiguration() {
        return modelConfiguration;
    }

}
