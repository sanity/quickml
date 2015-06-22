package quickml.supervised.tree.treeBuildContexts;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.google.common.collect.Sets;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class TreeContextBuilder<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    protected Scorer<VC> scorer;
    protected LeafBuilder<VC, N> leafBuilder;
    protected BranchingConditions<VC, N> branchingConditions;
    protected List<? extends BranchFinderBuilder<VC, N>> branchFinderBuilders = Lists.newArrayList();


    public abstract ValueCounterProducer<L, I, VC> getValueCounterProducer();

    public List<? extends BranchFinderBuilder<VC, N>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public BranchingConditions<VC, N> getBranchingConditions() {
        return branchingConditions;
    }


    public LeafBuilder<VC, N> getLeafBuilder() {
        return leafBuilder;
    }


    public TreeContextBuilder<L, I, VC, N> copy() {
        TreeContextBuilder<L, I, VC, N> copy = createTreeBuildContext();
        List<BranchFinderBuilder<VC, N>> copiedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<VC, N> branchFinderBuilder : this.branchFinderBuilders) {
            copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
        }
        copy.branchFinderBuilders = copiedBranchFinderBuilders;
        copy.branchingConditions = branchingConditions.copy();
        copy.scorer = scorer.copy();
        copy.leafBuilder = leafBuilder;
        return copy;
    }

    public boolean hasBranchFinderBuilder(BranchType branchType) {
        return getBranchFinderBuilder(branchType).isPresent();
    }

    public Optional<? extends BranchFinderBuilder<VC, N>> getBranchFinderBuilder(BranchType branchType) {
        for (BranchFinderBuilder branchFinderBuilder : branchFinderBuilders) {
            if (branchFinderBuilder.getBranchType().equals(branchType)) {
                return Optional.of(branchFinderBuilder);
            }
        }
        return Optional.absent();
    }

    public abstract TreeContextBuilder<L, I, VC, N> createTreeBuildContext();

    public abstract TreeContext<L, I, VC, N> buildContext(List<I> trainingData);

    public void update(final Map<String, Object> cfg) {
        if (cfg.containsKey(BRANCH_FINDER_BUILDERS.name()))
            branchFinderBuilders = (List<? extends BranchFinderBuilder<VC, N>>)cfg.get(BRANCH_FINDER_BUILDERS.name());
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<VC, N>) cfg.get(LEAF_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer<VC>) cfg.get(SCORER.name());
        if (cfg.containsKey(BRANCHING_CONDITIONS.name()))
            branchingConditions = (BranchingConditions<VC, N>) cfg.get(SCORER.name());
        scorer.update(cfg);
        branchingConditions.update(cfg);
        for (BranchFinderBuilder<VC, N> branchFinderBuilder : branchFinderBuilders) {
            branchFinderBuilder.update(cfg);
        }
    }
}

