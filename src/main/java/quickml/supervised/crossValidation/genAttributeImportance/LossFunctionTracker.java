package quickml.supervised.crossValidation.genAttributeImportance;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class LossFunctionTracker {

    private static final Logger logger = LoggerFactory.getLogger(LossFunctionTracker.class);


    // Map of loss function name to the running loss for that function
    private Map<String, RunningWeight> functionLossMap = Maps.newHashMap();
    private ClassifierLossFunction primaryLossFunction;

    public LossFunctionTracker(List<ClassifierLossFunction> lossFunctions) {
        this(getSecondaryLossFunctions(lossFunctions), lossFunctions.get(0));
    }

    public LossFunctionTracker(List<ClassifierLossFunction> lossFunctions, ClassifierLossFunction primaryLossFunction) {
        this.primaryLossFunction = primaryLossFunction;
        for (ClassifierLossFunction lossFunction : lossFunctions) {
            functionLossMap.put(lossFunction.getName(), new RunningWeight(lossFunction));
        }
        functionLossMap.put(primaryLossFunction.getName(), new RunningWeight(primaryLossFunction));
    }

    public void updateLosses(PredictionMapResults results) {
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

    private static List<ClassifierLossFunction> getSecondaryLossFunctions(List<ClassifierLossFunction> lossFunctions) {
        checkArgument(lossFunctions.size() > 0, "There must be at least one loss function supplied");
        return lossFunctions.subList(1, lossFunctions.size());
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
