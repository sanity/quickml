package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionLossFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class RegLossFunctionTracker {

    private static final Logger logger = LoggerFactory.getLogger(RegLossFunctionTracker.class);


    // Map of loss function name to the running loss for that function
    private Map<String, RunningWeight> functionLossMap = Maps.newHashMap();
    private RegressionLossFunction primaryLossFunction;

    public RegLossFunctionTracker(List<RegressionLossFunction> lossFunctions) {
        this(getSecondaryLossFunctions(lossFunctions), lossFunctions.get(0));
    }

    public RegLossFunctionTracker(List<RegressionLossFunction> lossFunctions, RegressionLossFunction primaryLossFunction) {
        this.primaryLossFunction = primaryLossFunction;
        for (RegressionLossFunction lossFunction : lossFunctions) {
            functionLossMap.put(lossFunction.getName(), new RunningWeight(lossFunction));
        }
        functionLossMap.put(primaryLossFunction.getName(), new RunningWeight(primaryLossFunction));
    }

    public void updateLosses(List<LabelPredictionWeight<Double, Double>> results) {
        for (RunningWeight runningWeight : functionLossMap.values()) {
            runningWeight.updateLosses(results);

        }
    }

    public Set<String> lossFunctionNames() {
        return functionLossMap.keySet();
    }

    public double getPrimaryLoss() {
        return getLossForFunction(primaryLossFunction.getName());
    }

    public double getLossForFunction(String lossFunction) {
        return functionLossMap.get(lossFunction).loss();
    }

    public void logLosses() {
        for (String functionName : functionLossMap.keySet()) {
            logger.info("Log function - {} - Loss - {}", functionName, functionLossMap.get(functionName).loss() );
        }
    }

    private static List<RegressionLossFunction> getSecondaryLossFunctions(List<RegressionLossFunction> lossFunctions) {
        checkArgument(lossFunctions.size() > 0, "There must be at least one loss function supplied");
        return lossFunctions.subList(1, lossFunctions.size());
    }

    class RunningWeight implements Comparable<RunningWeight> {

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        private RegressionLossFunction lossFunction;

        public RunningWeight(RegressionLossFunction lossFunction) {
            this.lossFunction = lossFunction;
        }

        public void updateLosses(List<LabelPredictionWeight<Double, Double>> results) {
            double totalWeight=0;
            for (LabelPredictionWeight<Double, Double> lpw : results) {
                totalWeight+=lpw.getWeight();
            }
            runningLoss += lossFunction.getLoss(results) * totalWeight;
            runningWeightOfValidationSet += totalWeight;
//            logger.info("loss for no missing attributes: {}",loss());
        }

        public double loss() {
            return runningWeightOfValidationSet > 0 ? runningLoss / runningWeightOfValidationSet : 0;
        }

        @Override
        public int compareTo(RunningWeight o) {
            return Double.compare(loss(), o.loss());
        }
    }


}
