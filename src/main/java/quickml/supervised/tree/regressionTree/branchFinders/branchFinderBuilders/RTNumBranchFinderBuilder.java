package quickml.supervised.tree.regressionTree.branchFinders.branchFinderBuilders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.regressionTree.attributeValueIgnoringStrategies.RegTreeAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.regressionTree.branchFinders.RTNumBranchFinder;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.util.Set;


/**
 * Created by alexanderhawk on 4/5/15.
 */

public class RTNumBranchFinderBuilder extends RTBranchFinderBuilder {
    private static final long serialVersionUID = 0L;

    @Override
    public  BranchFinderBuilder<MeanValueCounter> createBranchFinderBuilder() {
        return new RTNumBranchFinderBuilder();
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.RT_NUMERIC;
    }

    @Override
    public RTNumBranchFinder buildBranchFinder(MeanValueCounter meanValueCounter, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<MeanValueCounter> attributeValueIgnoringStrategy;
        if(attributeValueIgnoringStrategyBuilder==null) {
            RegTreeAttributeValueIgnoringStrategyBuilder regTreeAttributeValueIgnoringStrategyBuilder  = new RegTreeAttributeValueIgnoringStrategyBuilder(getMinAttributeOccurences());
            attributeValueIgnoringStrategy = regTreeAttributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(meanValueCounter);//TODO

        } else {
            attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(meanValueCounter); //TODO make an attributeValueIgnoringStrategy that takes a MeanValueCounter that informs upon the general ratio of interesting target values and uninteresting values.
        }
        return new RTNumBranchFinder(candidateAttributes, branchingConditions,
                scorerFactory, attributeValueIgnoringStrategy,
                attributeIgnoringStrategy);
    }
}


