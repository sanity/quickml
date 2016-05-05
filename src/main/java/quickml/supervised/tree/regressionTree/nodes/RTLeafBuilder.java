package quickml.supervised.tree.regressionTree.nodes;

import quickml.supervised.tree.decisionTree.nodes.DTLeaf;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;


/**
 * Created by alexanderhawk on 4/24/15.
 */
public class RTLeafBuilder implements LeafBuilder<MeanValueCounter> {
    private static final long serialVersionUID = 0L;

    public RTLeaf buildLeaf(Branch<MeanValueCounter> parent, MeanValueCounter valueCounter){
        return new RTLeaf(parent, valueCounter, parent==null || parent.isEmpty() ? 0 : parent.getDepth()+1);
    }

    @Override
    public LeafBuilder<MeanValueCounter> copy() {
        return new RTLeafBuilder();
    }
}
