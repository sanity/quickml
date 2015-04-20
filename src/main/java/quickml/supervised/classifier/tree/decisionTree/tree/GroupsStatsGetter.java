package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public class GroupsStatsGetter<GS extends TermStatistics> {
    private Set<BranchFinder<GS>> branchFinders;  //attributeValIgnoring Strategy needed here?
    AttributeIgnoringStrategy attributeIgnoringStrategy;  //how are we using this? Not sure?

    public GroupsStatsGetter(Set<BranchFinder<GS>> branchFinders, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.branchFinders = branchFinders;
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
    }

    public Set<BranchFinder<GS>> getBranchFinders() {
        return branchFinders;
    }

    //what function does this need. What does it do? Does this class loop through all the training data?  And get group stats
    //what are attribute group stats?
}
