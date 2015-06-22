package quickml.supervised.tree.decisionTree.treeBuildContexts;

import quickml.data.ClassifierInstance;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.decisionTree.nodes.DTNode;
import quickml.supervised.tree.decisionTree.reducers.DTreeBranchFinderAndReducer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.treeBuildContexts.TreeContext;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public class DTreeContext<I extends ClassifierInstance> extends TreeContext<I, ClassificationCounter, DTNode> {
    Set<Object> classifications;

    public DTreeContext(Set<Object> classifications,
                        BranchingConditions<ClassificationCounter, DTNode> branchingConditions,
                        Scorer scorer,
                        List<DTreeBranchFinderAndReducer<I>> branchFindersAndReducers,
                        LeafBuilder<ClassificationCounter, DTNode> leafBuilder,
                        ValueCounterProducer<I, ClassificationCounter> valueCounterProducer
    ) {
        super(branchingConditions, scorer, branchFindersAndReducers, leafBuilder, valueCounterProducer);
        this.classifications = classifications;
    }

    public Set<Object> getClassifications() {
        return classifications;
    }
}
