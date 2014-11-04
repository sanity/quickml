package quickml.supervised.crossValidation.crossValLossFunctions;

import com.google.common.collect.Maps;
import org.apache.mahout.math.function.Mult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/15/14.
 */
public class MultiLossFunctionWithModelConfigurations<P> implements CrossValLossFunction<P>{
   private static final Logger logger = LoggerFactory.getLogger(MultiLossFunctionWithModelConfigurations.class);
    /*
    This class stores a map of loss functions to LossForModelConfigurations (which stores a model's calculated loss and the model's configuration parameters).   */

    private Map<String, LossWithModelConfiguration> lossesWithModelConfigurations = Maps.newHashMap();
    private String primaryLossFunctionName;
    private double runningWeight;
    private Map<String, Double> runningLosses = Maps.newHashMap();
    private Map<String, CrossValLossFunction<P>> lossFunctions;
    private boolean normalizedAverages = false;


    public MultiLossFunctionWithModelConfigurations(Map<String, CrossValLossFunction<P>> lossFunctions, String primaryLossFunctionName) {
        this.lossFunctions = lossFunctions;
        this.primaryLossFunctionName = primaryLossFunctionName;
        for (String lossFunctionName : lossFunctions.keySet()) {
            runningLosses.put(lossFunctionName, 0.0);
        }
    }

    public MultiLossFunctionWithModelConfigurations(Map<String, CrossValLossFunction<P>> lossFunctions, String primaryLossFunctionName, Map<String, LossWithModelConfiguration> lossesWithModelConfigurations) {
        this.lossFunctions = lossFunctions;
        this.primaryLossFunctionName = primaryLossFunctionName;
        this.lossesWithModelConfigurations = lossesWithModelConfigurations;
        for (String lossFunctionName : lossFunctions.keySet()) {
            runningLosses.put(lossFunctionName, 0.0);
        }
    }


    public double getRunningWeight() {
        return runningWeight;
    }

    public double getLoss(List<LabelPredictionWeight<P>> labelPredictionWeights) {
        CrossValLossFunction<P> primaryLossFunction = lossFunctions.get(primaryLossFunctionName);
        return primaryLossFunction.getLoss(labelPredictionWeights);
    }

   public MultiLossFunctionWithModelConfigurations<P> mergeByBestLosses(MultiLossFunctionWithModelConfigurations<P> other) {
       Map<String, LossWithModelConfiguration> mergedLossesWithModelConfigurations = Maps.newHashMap();
       Map<String, LossWithModelConfiguration> lossesWithModelConfigurationsOther = other.getLossesWithModelConfigurations();
       for (String lossFunctionName : lossesWithModelConfigurations.keySet()) {
           if (!lossesWithModelConfigurationsOther.containsKey(lossFunctionName)) {
               mergedLossesWithModelConfigurations.put(lossFunctionName, lossesWithModelConfigurations.get(lossFunctionName));
               continue;
           }
           LossWithModelConfiguration lossWithModelConfiguration = lossesWithModelConfigurations.get(lossFunctionName);
           LossWithModelConfiguration lossWithModelConfigurationOther = lossesWithModelConfigurationsOther.get(lossFunctionName);

           double loss = lossWithModelConfiguration.getLoss();
           double lossOther = lossWithModelConfigurationOther.getLoss();

           if (loss > lossOther) {
               mergedLossesWithModelConfigurations.put(lossFunctionName, lossesWithModelConfigurationsOther.get(lossFunctionName));
           } else {
               mergedLossesWithModelConfigurations.put(lossFunctionName, lossesWithModelConfigurations.get(lossFunctionName));
           }
       }
       return new MultiLossFunctionWithModelConfigurations<P>(lossFunctions, primaryLossFunctionName, mergedLossesWithModelConfigurations);
   }

    public void updateRunningLosses(List<LabelPredictionWeight<P>> labelPredictionWeights){
        if (normalizedAverages) {
            throw new RuntimeException("should not be updating after the running average has been normalized");
        }
        double weightOfNewValidationSet = 0;
        for (LabelPredictionWeight<P> labelPredictionWeight : labelPredictionWeights)  {
            weightOfNewValidationSet+=labelPredictionWeight.getWeight();
        }
        this.runningWeight += weightOfNewValidationSet; //make sure to 0 this at start of a new cross validation
        for (String lossFunctionName :lossFunctions.keySet()) {
            double previousWeightedLoss = runningLosses.get(lossFunctionName);
            CrossValLossFunction<P> lossFunction = lossFunctions.get(lossFunctionName);
            double weightedLossFromValidationRun = lossFunction.getLoss(labelPredictionWeights) * weightOfNewValidationSet;
            runningLosses.put(lossFunctionName, weightedLossFromValidationRun + previousWeightedLoss);
        }
        for (String lossFunctionName : runningLosses.keySet()) {
            logger.info("Loss function: " + lossFunctionName + "running loss: " + runningLosses.get(lossFunctionName)/runningWeight + ".  Weight of val set: " + weightOfNewValidationSet);
        }
    }

    public void normalizeRunningAverages(){
        for (String lossFunctionName : lossesWithModelConfigurations.keySet()) {
            lossesWithModelConfigurations.get(lossFunctionName).setLoss(runningLosses.get(lossFunctionName)/runningWeight);
        }
        normalizedAverages = true;
    }

    public Map<String, LossWithModelConfiguration> getLossesWithModelConfigurations() {
        return lossesWithModelConfigurations;
    }

}
