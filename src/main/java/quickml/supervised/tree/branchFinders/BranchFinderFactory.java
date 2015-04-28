package quickml.supervised.tree.branchFinders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.decisionTree.tree.BranchType;
import quickml.supervised.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.tree.decisionTree.tree.TerminationConditions;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/21/15.
 */
public abstract class BranchFinderFactory<TS extends TermStatistics> {
    private BranchType branchType;

    public BranchType getBranchType() {
        return branchType;
    }

    public abstract BranchFinder<TS> createBranchFinder(Set<String> candidateAttributes, TerminationConditions<TS> terminationConditions,
                                        Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy,
                                        AttributeIgnoringStrategy attributeIgnoringStrategy);
}
