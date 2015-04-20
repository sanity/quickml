package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.TreeConfig;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<GS extends TermStatistics> {
    TreeConfig treeConfig;

    public abstract BranchFinderBuilder<GS> copy();

    public abstract void  update(Map<String, Object> cfg);

    public abstract BranchFinder<GS> buildBranchFinder();

}

