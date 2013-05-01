package quickdt.randomForest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickdt.Branch;
import quickdt.Instance;
import quickdt.Node;
import quickdt.TreeBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomForestBuilder {
    private final TreeBuilder treeBuilder;
    private final int numTrees;

    public RandomForestBuilder() {
        this(new TreeBuilder(), 8);
    }

    public RandomForestBuilder(TreeBuilder treeBuilder, int numTrees) {
        this.treeBuilder = treeBuilder;
        this.numTrees = numTrees;
    }

    public RandomForest buildRandomForest(final Iterable<Instance> trainingData) {
        return buildRandomForest(trainingData, Integer.MAX_VALUE, 1.0);
    }

    public RandomForest buildRandomForest(final Iterable<Instance> trainingData, final int maxDepth, final double minProbability) {
        List<Node> trees = Lists.newArrayListWithCapacity(numTrees);

        Set<String> excludeAttributes = Sets.newHashSet();
        for (int treeIx = 0; treeIx < numTrees; treeIx++) {
            Node tree = treeBuilder.buildTree(trainingData, maxDepth, minProbability, excludeAttributes, 1);
            if (tree instanceof Branch) {
                Branch branch = (Branch) tree;
                excludeAttributes.add(branch.attribute);
            }
            trees.add(tree);
        }

        return new RandomForest(trees);
    }
}
