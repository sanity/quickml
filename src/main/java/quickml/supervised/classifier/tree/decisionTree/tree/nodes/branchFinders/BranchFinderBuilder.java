package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.tree.DataForTheAssessmentOfSplitValidity;
import quickml.supervised.classifier.tree.decisionTree.tree.TerminationConditions;
import quickml.supervised.classifier.tree.decisionTree.tree.TreeConfig;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<L, T extends InstanceWithAttributesMap<L>, Tr extends TreeConfig> {
    TreeConfig treeConfig;

    public abstract BranchFinderBuilder<L, T, Tr> copy();

    public abstract void  update(Map<String, Object> cfg);

    public abstract BranchFinder<L, T> buildBranchFinder();

}

