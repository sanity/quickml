package quickml.supervised.tree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 6/18/15.
 */
public interface Node<VC extends ValueCounter<VC>> {


    @Override
    boolean equals(final Object obj);

    @Override
    int hashCode();
    //last 2 are optional
    void calcLeafDepthStats(LeafDepthStats stats);

    /**
     * Return the number of nodes in this decision oldTree.
     *
     * @return
     */
    int getSize();

    Leaf<VC> getLeaf(AttributesMap attributes);
    Node<VC> getParent();
}
