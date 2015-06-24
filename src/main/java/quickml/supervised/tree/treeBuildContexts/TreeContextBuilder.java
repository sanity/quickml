package quickml.supervised.tree.treeBuildContexts;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.scorers.Scorer;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class TreeContextBuilder<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    protected Scorer<VC> scorer;
    protected LeafBuilder<VC, N> leafBuilder;
    protected BranchingConditions<VC, N> branchingConditions;
    protected List<? extends BranchFinderBuilder<VC, N>> branchFinderBuilders = Lists.newArrayList();
    protected Map<String, Object> config = Maps.newHashMap();

    public abstract ValueCounterProducer<I, VC> getValueCounterProducer();

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


    public TreeContextBuilder<I, VC, N> copy() {

        TreeContextBuilder<I, VC, N> copy = createTreeBuildContext();
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

    public Optional<BranchFinderBuilder<VC, N>> getBranchFinderBuilder(BranchType branchType) {
        for (BranchFinderBuilder<VC, N> branchFinderBuilder : branchFinderBuilders) {
            if (branchFinderBuilder.getBranchType().equals(branchType)) {
                return Optional.of(branchFinderBuilder);
            }
        }
        return Optional.absent();
    }

    public abstract TreeContextBuilder<I, VC, N> createTreeBuildContext();

    public abstract TreeContext<I, VC, N> buildContext(List<I> trainingData);

    public void updateBuilderConfig(final Map<String, Object> cfg) {
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
        this.config = cfg;
    }
}

