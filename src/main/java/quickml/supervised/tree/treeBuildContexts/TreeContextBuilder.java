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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class TreeContextBuilder<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> {
    protected Scorer<VC> scorer;
    protected LeafBuilder<VC> leafBuilder;
    protected BranchingConditions<VC> branchingConditions;
    protected List<? extends BranchFinderBuilder<VC>> branchFinderBuilders = Lists.newArrayList();
    protected Map<String, Serializable> config = Maps.newHashMap();

    public abstract ValueCounterProducer<I, VC> getValueCounterProducer();

    public List<? extends BranchFinderBuilder<VC>> getBranchFinderBuilders() {
        //TODO consider making this getter access the config, and removing the field altogether.
        return branchFinderBuilders;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public BranchingConditions<VC> getBranchingConditions() {
        return branchingConditions;
    }


    public LeafBuilder<VC> getLeafBuilder() {
        return leafBuilder;
    }


    public TreeContextBuilder<I, VC> copy() {

        TreeContextBuilder<I, VC> copy = createTreeBuildContext();
        List<BranchFinderBuilder<VC>> copiedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<VC> branchFinderBuilder : this.branchFinderBuilders) {
            copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
        }
        copy.branchFinderBuilders = copiedBranchFinderBuilders;
        copy.branchingConditions = branchingConditions.copy();
        copy.scorer = scorer.copy();
        copy.leafBuilder = leafBuilder.copy();
        return copy;
    }

    public boolean hasBranchFinderBuilder(BranchType branchType) {
        return getBranchFinderBuilder(branchType).isPresent();
    }

    public Optional<BranchFinderBuilder<VC>> getBranchFinderBuilder(BranchType branchType) {
        for (BranchFinderBuilder<VC> branchFinderBuilder : branchFinderBuilders) {
            if (branchFinderBuilder.getBranchType().equals(branchType)) {
                return Optional.of(branchFinderBuilder);
            }
        }
        return Optional.absent();
    }

    public abstract TreeContextBuilder<I, VC> createTreeBuildContext();

    public abstract TreeContext<I, VC> buildContext(List<I> trainingData);

    public void updateBuilderConfig(final Map<String, Serializable> cfg) {
        if (cfg.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            branchFinderBuilders = (List<? extends BranchFinderBuilder<VC>>) cfg.get(BRANCH_FINDER_BUILDERS.name());
            if (branchFinderBuilders != null && !branchFinderBuilders.isEmpty())
                for (BranchFinderBuilder<VC> branchFinderBuilder : branchFinderBuilders) {
                    branchFinderBuilder.update(cfg);
                }
        }
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<VC>) cfg.get(LEAF_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer<VC>) cfg.get(SCORER.name());
        if (cfg.containsKey(BRANCHING_CONDITIONS.name()))
            branchingConditions = (BranchingConditions<VC>) cfg.get(BRANCHING_CONDITIONS.name());
        if (scorer != null) {
            scorer.update(cfg);
        }
        if (branchingConditions != null) {
            branchingConditions.update(cfg);
        }

        this.config = copyConfig(cfg);
    }

    public abstract Map<String, Serializable> copyConfig(Map<String, Serializable> config);

}