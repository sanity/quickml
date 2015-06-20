package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.summaryStatistics.AttributeStatisticsProducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.terminationConditions.BranchingConditions;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/24/15.
 */

public abstract class BranchFinder<VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    protected Set<String> candidateAttributes;
    protected BranchingConditions<VC, N> branchingConditions;
    protected Scorer<VC> scorer;
    protected AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy;
    protected AttributeIgnoringStrategy attributeIgnoringStrategy;
    protected BranchType branchType;

    //branch finder should not take so many arguments. Facade could simplify this.
    public BranchFinder(Collection<String> candidateAttributes, BranchingConditions<VC, N> branchingConditions, Scorer<VC> scorer, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        this.candidateAttributes = Sets.newHashSet(candidateAttributes);
        this.branchingConditions = branchingConditions;
        this.scorer = scorer;
        this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
        this.branchType = branchType;
    }
    //what flexibility am i actually creating

    public BranchType getBranchType() {
        return branchType;
    }

    protected List<String> getCandidateAttributesWithIgnoringApplied(Branch<VC, N> parent) {
        List<String> attributes = Lists.newArrayList();
        for (String attribute : candidateAttributes) {
            if (!attributeIgnoringStrategy.ignoreAttribute(attribute, parent)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public Optional<? extends Branch<VC, N>> findBestBranch(Branch<VC, N> parent, AttributeStatisticsProducer<VC> attributeStatisticsProducer) {
        double bestScore = 0;
        Optional<? extends Branch<VC, N>> bestBranchOptional = Optional.absent();
        for (String attribute : getCandidateAttributesWithIgnoringApplied(parent)) {
            AttributeStats<VC> attributeStats = attributeStatisticsProducer.getAttributeStats(attribute);
            Optional<? extends Branch<VC, N>> thisBranchOptional = getBranch(parent, attributeStats);
            if (thisBranchOptional.isPresent()) {
                Branch<VC, N> thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {
                    bestScore = thisBranch.score;
                    bestBranchOptional = thisBranchOptional;
                }
            }
        }
        return bestBranchOptional;
    }

    public abstract Optional<? extends Branch<VC, N>> getBranch(Branch<VC, N> parent, AttributeStats<VC> attributeStats);
}
