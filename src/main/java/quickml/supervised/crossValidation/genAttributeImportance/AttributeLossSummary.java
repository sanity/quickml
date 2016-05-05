package quickml.supervised.crossValidation.genAttributeImportance;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AttributeLossSummary {


    private List<AttributeLossTracker> lossTrackers;

    public AttributeLossSummary(List<AttributeLossTracker> lossTrackers) {
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
        AttributeLossTracker optimalSet = lossTrackers.get(0);
        for (AttributeLossTracker lossTracker : lossTrackers) {
            if (isCloserToOptimalSize(n, optimalSet, lossTracker)
                    || (equallyCloseToOptimalSet(n, optimalSet, lossTracker) && lossIsBetter(optimalSet, lossTracker))) {
                optimalSet = lossTracker;
            }
        }
        return optimalSet.getOrderedLosses();
    }

    private boolean lossIsBetter(AttributeLossTracker optimalSet, AttributeLossTracker lossTracker) {
        return lossTracker.getOverallLoss() < optimalSet.getOverallLoss();
    }

    private boolean equallyCloseToOptimalSet(int n, AttributeLossTracker optimalSet, AttributeLossTracker lossTracker) {
        return (Math.abs(lossTracker.getOrderedAttributes().size() - n) == Math.abs(optimalSet.getOrderedAttributes().size() - n));
    }

    private boolean isCloserToOptimalSize(int n, AttributeLossTracker optimalSet, AttributeLossTracker lossTracker) {
        return Math.abs(lossTracker.getOrderedAttributes().size() - n) < Math.abs(optimalSet.getOrderedAttributes().size() - n);
    }

    private void sortTrackers(List<AttributeLossTracker> lossTrackers) {
        Collections.sort(lossTrackers, new Comparator<AttributeLossTracker>() {
            @Override
            public int compare(AttributeLossTracker o1, AttributeLossTracker o2) {
                return Double.compare(o1.getOverallLoss(), o2.getOverallLoss());
            }
        });
    }


}
