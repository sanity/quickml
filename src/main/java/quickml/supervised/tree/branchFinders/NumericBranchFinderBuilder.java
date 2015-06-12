package quickml.supervised.tree.branchFinders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.constants.BranchType;

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
        return new NumBranch(candidateAttributesForNumericBranch, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }
}
