package quickdt.experiments;

import quickdt.predictiveModels.downsamplingPredictiveModel.Utils;

public class ClicksAndImps {
    double clicks = 0;
    double imps = 0;
    double ctr = 0;
    double correctedCtr = 0;
    public void setCtr() {
        ctr = clicks/ Math.max(1.0, imps);
        setCorrectedCtr();
    }
    public void setCorrectedCtr() {
        correctedCtr = Utils.correctProbability(.99, ctr);
    }
}
