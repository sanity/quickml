package quickml.supervised.tree.regressionTree.nodes;


import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.NumBranch;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class RTNumBranch extends NumBranch<MeanValueCounter> {

    public RTNumBranch(Branch<MeanValueCounter> parent, String attribute, double probabilityOfTrueChild, double score, MeanValueCounter termStatistics, double threshold) {
        super(parent, attribute, probabilityOfTrueChild, score, termStatistics, threshold);

    }
}
