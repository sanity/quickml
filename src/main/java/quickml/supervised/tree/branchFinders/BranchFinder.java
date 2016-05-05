package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.reducers.AttributeStatisticsProducer;
import quickml.supervised.tree.scorers.ScorerFactory;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.*;

/**
 * Created by alexanderhawk on 3/24/15.
 */

public abstract class BranchFinder<VC extends ValueCounter<VC>> {
    protected Set<String> candidateAttributes;
    protected BranchingConditions<VC> branchingConditions;
    protected ScorerFactory<VC> scorerFactory;
    protected AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy;
    protected AttributeIgnoringStrategy attributeIgnoringStrategy;

    public BranchFinder(Collection<String> candidateAttributes, BranchingConditions<VC> branchingConditions, ScorerFactory<VC> scorerFactory, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.candidateAttributes = Sets.newHashSet(candidateAttributes);
        this.branchingConditions = branchingConditions;
        this.scorerFactory = scorerFactory;
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

    protected List<String> alternativeGetCandidateAttributesWithIgnoringApplied(Branch<VC> parent) {
    double ignoreProb = ((IgnoreAttributesWithConstantProbability) attributeIgnoringStrategy).getIgnoreAttributeProbability();
    ArrayList<String> candidates = Lists.newArrayList(candidateAttributes);
        if (ignoreProb == 0.0) {
        return candidates;
    }
        int numTrialAttributes = (int)((1.0-ignoreProb)*candidates.size());

        //p_of_a_single_colision at i= frac_in_return_set (fi).  The expected num collisions at point i is ei.
        // ei = p of just 1 collision = p(1-p), prob of just 2 collisions: = (1-p)p^2, n collisions = (1-p)p^n
        //e_i = expected num collisions sum(n*p(1-p)^n, n=0, n=N), N=in collection.=>  e_ = p/(1-p) *d/dB( sum B^n, n=1, n=N+1), B=1-p
        //p/(1-p)*d/dB[(1-B^(N+1))/(1-B)]=
        //sum(ei, i=1, num_elements_to_be_returned),

        //O(N) way of shuffling the attributes to make all permutations equally likely.
    Collections.shuffle(candidates);

    return candidates.subList(0,numTrialAttributes);


    }



    public Optional<? extends Branch<VC>> findBestBranch(Branch<VC> parent, AttributeStatisticsProducer<VC> attributeStatisticsProducer) {
        double bestScore = 0;
        Optional<? extends Branch<VC>> bestBranchOptional = Optional.absent();
       // for (String attribute : getCandidateAttributesWithIgnoringApplied(parent)) {
        for (String attribute : alternativeGetCandidateAttributesWithIgnoringApplied(parent)) {
            Optional<AttributeStats<VC>> attributeStatsOptional = attributeStatisticsProducer.getAttributeStats(attribute);
            if (!attributeStatsOptional.isPresent()) {
                continue;
            }
            AttributeStats<VC> attributeStats = attributeStatsOptional.get();
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
