package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.reducers.AttributeStatisticsProducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.scorers.Scorer;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/24/15.
 */

public abstract class BranchFinder<VC extends ValueCounter<VC>> {
    protected Set<String> candidateAttributes;
    protected BranchingConditions<VC> branchingConditions;
    protected Scorer<VC> scorer;
    protected AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy;
    protected AttributeIgnoringStrategy attributeIgnoringStrategy;

    public BranchFinder(Collection<String> candidateAttributes, BranchingConditions<VC> branchingConditions, Scorer<VC> scorer, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.candidateAttributes = Sets.newHashSet(candidateAttributes);
        this.branchingConditions = branchingConditions;
        this.scorer = scorer;
        this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
    }

    public abstract BranchType getBranchType();

    protected List<String> getCandidateAttributesWithIgnoringApplied(Branch<VC> parent) {
        List<String> attributes = Lists.newArrayList();
        for (String attribute : candidateAttributes) {
            if (!attributeIgnoringStrategy.ignoreAttribute(attribute, parent)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public Optional<? extends Branch<VC>> findBestBranch(Branch<VC> parent, AttributeStatisticsProducer<VC> attributeStatisticsProducer) {
        double bestScore = 0;
        Optional<? extends Branch<VC>> bestBranchOptional = Optional.absent();
        for (String attribute : getCandidateAttributesWithIgnoringApplied(parent)) {
            AttributeStats<VC> attributeStats = attributeStatisticsProducer.getAttributeStats(attribute);
            Optional<? extends Branch<VC>> thisBranchOptional = getBranch(parent, attributeStats);
            if (thisBranchOptional.isPresent()) {
                Branch<VC> thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {
                    bestScore = thisBranch.score;
                    bestBranchOptional = thisBranchOptional;
                }
            }
        }
        return bestBranchOptional;
    }

    public abstract Optional<? extends Branch<VC>> getBranch(Branch<VC> parent, AttributeStats<VC> attributeStats);
}
