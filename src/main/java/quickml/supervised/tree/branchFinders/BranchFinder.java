package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.AttributeStatisticsProducer;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.scorers.Scorer;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.terminationConditions.TerminationConditions;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/24/15.
 */

public abstract class BranchFinder<TS extends TermStatsAndOperations<TS>> {
    protected Set<String> candidateAttributes;
    protected TerminationConditions<TS> terminationConditions;
    protected Scorer<TS> scorer;
    protected AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy;
    protected AttributeIgnoringStrategy attributeIgnoringStrategy;
    protected BranchType branchType;

    //branch finder should not take so many arguments. Facade could simplify this.
    public BranchFinder(Collection<String> candidateAttributes, TerminationConditions<TS> terminationConditions, Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        this.candidateAttributes = Sets.newHashSet(candidateAttributes);
        this.terminationConditions = terminationConditions;
        this.scorer = scorer;
        this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
        this.branchType = branchType;
    }
    //what flexibility am i actually creating

    public BranchType getBranchType() {
        return branchType;
    }

    protected List<String> getCandidateAttributesWithIgnoringApplied(Branch<TS> parent) {
        List<String> attributes = Lists.newArrayList();
        for (String attribute : candidateAttributes) {
            if (!attributeIgnoringStrategy.ignoreAttribute(attribute, parent)) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public Optional<? extends Branch<TS>> findBestBranch(Branch<TS> parent, AttributeStatisticsProducer<TS> attributeStatisticsProducer) {
        double bestScore = 0;
        Optional<? extends Branch<TS>> bestBranchOptional = Optional.absent();
        for (String attribute : getCandidateAttributesWithIgnoringApplied(parent)) {
            AttributeStats<TS> attributeStats = attributeStatisticsProducer.getAttributeStats(attribute);
            Optional<? extends Branch<TS>> thisBranchOptional = getBranch(parent, attributeStats);
            if (thisBranchOptional.isPresent()) {
                Branch<TS> thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {
                    bestScore = thisBranch.score;
                    bestBranchOptional = thisBranchOptional;
                }
            }
        }
        return bestBranchOptional;
    }

    public abstract Optional<? extends Branch<TS>> getBranch(Branch<TS> parent, AttributeStats<TS> attributeStats);
}
