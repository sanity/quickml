package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.BranchType;
import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.AttributeStatisticsProducer;
import quickml.supervised.classifier.tree.decisionTree.tree.TerminationConditions;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

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

    public abstract Optional<? extends Branch<TS>> getBranch(Branch parent, AttributeStats<TS> attributeStats);
}
