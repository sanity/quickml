package quickml.supervised.tree.decisionTree.nodes;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.NumBranch;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class DTNumBranch extends NumBranch<ClassificationCounter>{

    public DTNumBranch(Branch<ClassificationCounter> parent, String attribute, double probabilityOfTrueChild, double score, ClassificationCounter termStatistics, double threshold) {
        super(parent, attribute, probabilityOfTrueChild, score, termStatistics, threshold);

    }
}
