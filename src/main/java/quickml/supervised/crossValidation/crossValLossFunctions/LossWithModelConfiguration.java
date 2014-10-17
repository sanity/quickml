package quickml.supervised.crossValidation.crossValLossFunctions;

import java.io.Serializable;
import java.util.Map;

public class LossWithModelConfiguration {
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
