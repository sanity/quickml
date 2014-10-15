package quickml.supervised.crossValidation.crossValLossFunctions;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/15/14.
 */
public class MultiLossFunctionModelConfigurations<P> implements CrossValLossFunction<P>{
    private HashMap<CrossValLossFunction<P>, LossForModelConfiguration> lossesAndModelConfigurations = Maps.newHashMap();
    CrossValLossFunction<P> primaryLossFunction;

    public MultiLossFunctionModelConfigurations(List<CrossValLossFunction<P>> lossFunctions) {
        for (CrossValLossFunction<P> lossFunction : lossFunctions)
            lossesAndModelConfigurations.put(lossFunction, new LossForModelConfiguration());
        primaryLossFunction = lossFunctions.get(0);
    }

    public MultiLossFunctionModelConfigurations(List<CrossValLossFunction<P>> lossFunctions, CrossValLossFunction<P> primaryLossFunction) {
        for (CrossValLossFunction<P> lossFunction : lossFunctions)
            lossesAndModelConfigurations.put(lossFunction, new LossForModelConfiguration());
        this.primaryLossFunction = primaryLossFunction;
    }


    public double getLoss(List<LabelPredictionWeight<P>> labelPredictionWeights) {
        return primaryLossFunction.getLoss(labelPredictionWeights);
    }

    public void updateLossesAndModelConfigurations(List<LabelPredictionWeight<P>> labelPredictionWeights, Map<String, Serializable> modelConfiguration){
        for (CrossValLossFunction<P> lossFunction : lossesAndModelConfigurations.keySet()) {
            double loss = lossFunction.getLoss(labelPredictionWeights);
            if ( loss < lossesAndModelConfigurations.get(lossFunction).getLoss()) {
                lossesAndModelConfigurations.get(lossFunction).setLoss(loss);
                lossesAndModelConfigurations.get(lossFunction).setConfiguration(modelConfiguration);
            }
        }
    }

    public HashMap<CrossValLossFunction<P>, LossForModelConfiguration> getLossesAndModelConfigurations () {
        return lossesAndModelConfigurations;
    }

}

class LossForModelConfiguration{
    double loss;
    Map<String, Serializable> modelConfiguration;

    public LossForModelConfiguration(){}

    public LossForModelConfiguration(double loss, Map<String, Serializable> modelConfiguration) {
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
