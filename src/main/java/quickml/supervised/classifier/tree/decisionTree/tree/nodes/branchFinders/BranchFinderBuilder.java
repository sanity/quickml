package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.BranchType;
import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.TerminationConditions;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<TS extends TermStatsAndOperations<TS>, D extends DataProperties> {
    private TerminationConditions<TS> terminationConditions;
    private Scorer<TS> scorer;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private AttributeValueIgnoringStrategyBuilder<TS, D> attributeValueIgnoringStrategyBuilder;
    private BranchType branchType;


    public BranchType getBranchType() {
        return branchType;
    }

    public AttributeIgnoringStrategy getAttributeIgnoringStrategy() {
        return attributeIgnoringStrategy;
    }

    public void setTerminationConditions(TerminationConditions<TS> terminationConditions) {
        this.terminationConditions = terminationConditions;
    }

    public void setScorer(Scorer<TS> scorer) {
        this.scorer = scorer;
    }


    public void setAttributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<TS, D> attributeValueIgnoringStrategyBuilder) {
        this.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder;
    }

    public void setAttributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
    }

    public abstract void update(Map<String, Object> cfg);

    public abstract BranchFinderBuilder<TS, D> copy();

    public abstract BranchFinder<TS> buildBranchFinder(D dataProperties);
/*        AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(dataProperties);
        final Set<String> candidateAttributes = dataProperties.getCandidateAttributesByBranchType().get(branchFinderFactory.getBranchType());
        return branchFinderFactory.createBranchFinder(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }
  */
    //what is the problem.  Different Branch finders have different properties...which i need to copy and update.Think the branch finderBuilder is


}

