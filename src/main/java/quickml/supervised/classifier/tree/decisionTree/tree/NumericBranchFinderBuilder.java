package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.ClassifierDataProperties;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.NumericBranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatsAndOperations;

import java.util.Set;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public class NumericBranchFinderBuilder<TS extends TermStatsAndOperations<TS>, D extends DataProperties> extends BranchFinderBuilder<TS, D> {

    @Override
    public BranchFinderBuilder<TS, D> createBranchFinderBuilder() {
        return new NumericBranchFinderBuilder<>();
    }


    @Override
    public BranchFinder<TS> buildBranchFinder(D dataProperties) { //would be better if it took a configFacade, which i could extend have problem with number of ordinal splits.
        Set<String> candidateAttributesForNumericBranch = dataProperties.getCandidateAttributesForBranchType(BranchType.NUMERIC);
        AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(dataProperties);
        return new NumericBranchFinder<TS>(candidateAttributesForNumericBranch, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }
}
