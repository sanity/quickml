package quickml.supervised.crossValidation.attributeImportance;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegAttributeLossSummary {


    private List<RegAttributeLossTracker> lossTrackers;

    public RegAttributeLossSummary(List<RegAttributeLossTracker> lossTrackers) {
        this.lossTrackers = lossTrackers;
        sortTrackers(lossTrackers);
    }

    public List<String> getOptimalAttributes() {
        return lossTrackers.get(0).getOrderedAttributes();
    }


    /**
     * Return the list that is closest in getSize to the desired getSize
     * If two sets are equidistant from the desired getSize, return the one with the lowest loss
     * @param n
     * @return
     */
    public List<AttributeWithLoss> getMaximalSet(int n) {
        RegAttributeLossTracker optimalSet = lossTrackers.get(0);
        for (RegAttributeLossTracker lossTracker : lossTrackers) {
            if (isCloserToOptimalSize(n, optimalSet, lossTracker)
                    || (equallyCloseToOptimalSet(n, optimalSet, lossTracker) && lossIsBetter(optimalSet, lossTracker))) {
                optimalSet = lossTracker;
            }
        }
        return optimalSet.getOrderedLosses();
    }

    private boolean lossIsBetter(RegAttributeLossTracker optimalSet, RegAttributeLossTracker lossTracker) {
        return lossTracker.getOverallLoss() < optimalSet.getOverallLoss();
    }

    private boolean equallyCloseToOptimalSet(int n, RegAttributeLossTracker optimalSet, RegAttributeLossTracker lossTracker) {
        return (Math.abs(lossTracker.getOrderedAttributes().size() - n) == Math.abs(optimalSet.getOrderedAttributes().size() - n));
    }

    private boolean isCloserToOptimalSize(int n, RegAttributeLossTracker optimalSet, RegAttributeLossTracker lossTracker) {
        return Math.abs(lossTracker.getOrderedAttributes().size() - n) < Math.abs(optimalSet.getOrderedAttributes().size() - n);
    }

    private void sortTrackers(List<RegAttributeLossTracker> lossTrackers) {
        Collections.sort(lossTrackers, new Comparator<RegAttributeLossTracker>() {
            @Override
            public int compare(RegAttributeLossTracker o1, RegAttributeLossTracker o2) {
                return Double.compare(o1.getOverallLoss(), o2.getOverallLoss());
            }
        });
    }


}
