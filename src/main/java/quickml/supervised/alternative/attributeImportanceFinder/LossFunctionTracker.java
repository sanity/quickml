package quickml.supervised.alternative.attributeImportanceFinder;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.alternative.crossvalidation.ClassifierLossFunction;
import quickml.supervised.alternative.crossvalidation.PredictionMapResults;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LossFunctionTracker {

    private static final Logger logger = LoggerFactory.getLogger(LossFunctionTracker.class);


    // Map of loss function name to the running loss for that function
    private Map<String, RunningWeight> functionLossMap = Maps.newHashMap();

    public LossFunctionTracker(List<ClassifierLossFunction> lossFunctions) {
        for (ClassifierLossFunction lossFunction : lossFunctions) {
            functionLossMap.put(lossFunction.getName(), new RunningWeight(lossFunction));
        }
    }

    public void updateLosses(PredictionMapResults results) {
        for (RunningWeight runningWeight : functionLossMap.values()) {
            runningWeight.updateLosses(results);
        }
    }

    public Set<String> lossFunctionNames() {
        return functionLossMap.keySet();
    }

    public double getLossForFunction(String lossFunction) {
        return functionLossMap.get(lossFunction).loss();
    }

    public void logLosses() {
        for (String functionName : functionLossMap.keySet()) {
            logger.info("Log function - {} - Loss - {}", functionName, functionLossMap.get(functionName).loss() );
        }
    }

    class RunningWeight implements Comparable<RunningWeight> {

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        private ClassifierLossFunction lossFunction;

        public RunningWeight(ClassifierLossFunction lossFunction) {
            this.lossFunction = lossFunction;
        }

        public void updateLosses(PredictionMapResults results) {
            runningLoss += lossFunction.getLoss(results) * results.totalWeight();
            runningWeightOfValidationSet += results.totalWeight();
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
