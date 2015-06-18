package quickml.supervised.tree.branchFinders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.constants.BranchType;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class CategoricalBranchFinderBuilder<VC extends ValueCounter<VC>, D extends DataProperties> extends BranchFinderBuilder<VC, D> {

    @Override
    public BranchFinderBuilder<VC, D> createBranchFinderBuilder() {
        return new CategoricalBranchFinderBuilder<>();
    }


    @Override
    public BranchFinder<VC> buildBranchFinder(D dataProperties) { //would be better if it took a configFacade, which i could extend have problem with number of ordinal splits.
        Set<String> candidateAttributesForNumericBranch = dataProperties.getCandidateAttributesForBranchType(BranchType.NUMERIC);
        AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(dataProperties);
        return new NumericBranchFinder<VC>(candidateAttributesForNumericBranch, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }
}
