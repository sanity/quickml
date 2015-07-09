package quickml.supervised.tree.treeBuildContexts;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.scorers.ScorerFactory;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.LeafBuilder;
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

    protected Map<String, Serializable> config = Maps.newHashMap();

    public abstract ValueCounterProducer<I, VC> getValueCounterProducer();

    public List<? extends BranchFinderBuilder<VC>> getBranchFinderBuilders() {
        //TODO consider making this getter access the config, and removing the field altogether.
        if (config.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            return (List<? extends BranchFinderBuilder<VC>>) config.get(BRANCH_FINDER_BUILDERS.name());
        } else {
            List<? extends BranchFinderBuilder<VC>> emptyList = Lists.newArrayList();
            return emptyList;
        }
    }

    public ScorerFactory<VC> getScorerFactory() {
            return (ScorerFactory<VC>) config.get(SCORER_FACTORY.name());
    }

    public BranchingConditions<VC> getBranchingConditions() {
            return (BranchingConditions<VC>) config.get(BRANCHING_CONDITIONS.name());
    }


    public LeafBuilder<VC> getLeafBuilder() {
            return (LeafBuilder<VC>) config.get(LEAF_BUILDER.name());
    }


    public TreeContextBuilder<I, VC> copy() {
        TreeContextBuilder<I, VC> copy = createTreeBuildContext();
        copy.config = copyConfig(this.config);
        return copy;
    }

    public boolean hasBranchFinderBuilder(BranchType branchType) {
        return getBranchFinderBuilder(branchType).isPresent();
    }

    public Optional<BranchFinderBuilder<VC>> getBranchFinderBuilder(BranchType branchType) {
        for (BranchFinderBuilder<VC> branchFinderBuilder : getBranchFinderBuilders()) {
            if (branchFinderBuilder.getBranchType().equals(branchType)) {
                return Optional.of(branchFinderBuilder);
            }
        }
        return Optional.absent();
    }

    public abstract TreeContextBuilder<I, VC> createTreeBuildContext();

    public abstract TreeContext<I, VC> buildContext(List<I> trainingData);

    public void updateBuilderConfig(final Map<String, Serializable> cfg) {
        this.config = copyConfig(cfg);
    }

    public abstract Map<String, Serializable> copyConfig(Map<String, Serializable> config);

}
