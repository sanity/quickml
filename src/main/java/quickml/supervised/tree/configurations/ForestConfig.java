package quickml.supervised.tree.configurations;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.constants.ForestOptions;
import quickml.supervised.tree.decisionTree.tree.TermStatistics;

import java.util.Map;

import static quickml.supervised.tree.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 4/15/15.
 */
public class ForestConfig <L, T extends InstanceWithAttributesMap<L>, GS extends TermStatistics, Tr extends TreeConfig<L, T, GS, Tr >> {
    TreeConfig<L, T, GS, Tr> treeConfig;
    private int numTrees = 1;
    private Optional<Double> downSamplingTargetMinorityProportion = Optional.absent();

    public Optional<Double> getDownSamplingTargetMinorityProportion() {
        return downSamplingTargetMinorityProportion;
    }
    public ForestConfig<L, T, GS, Tr> downSamplingTargetMinorityProportion(Optional<Double> downSamplingTargetMinorityProportion) {
        this.downSamplingTargetMinorityProportion = downSamplingTargetMinorityProportion;
        return this;
    }

    public ForestConfig<L, T, GS, Tr> treeConfig(TreeConfig<L, T, GS, Tr> treeConfig) {
        this.treeConfig = treeConfig;
        return this;
    }

    public ForestConfig<L, T, GS, Tr> numTrees(int numTrees) {
        this.numTrees = numTrees;
        return this;
    }

    public ForestConfig<L, T, GS, Tr>  copy() {
        ForestConfig<L, T, GS, Tr> copy = new ForestConfig<L, T, GS, Tr> ();
        copy.treeConfig = treeConfig.copy();
        copy.numTrees = numTrees;
        copy.downSamplingTargetMinorityProportion = downSamplingTargetMinorityProportion;

        return copy;
    }

    public void update(final Map<String, Object> cfg) {

        if (cfg.containsKey(ForestOptions.NUM_TREES.name()))
            numTrees = (Integer) cfg.get(ForestOptions.NUM_TREES.name());
        if (cfg.containsKey(ForestOptions.DOWNSAMPLING_TARGET_MINORITY_PROPORTION.name()))
            downSamplingTargetMinorityProportion = (Optional<Double>) cfg.get(ForestOptions.DOWNSAMPLING_TARGET_MINORITY_PROPORTION.name());
    }

}
