package quickdt.randomForest;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickdt.*;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomForestBuilder implements PredictiveModelBuilder<RandomForest> {
    private final TreeBuilder treeBuilder;
    private int numTrees = 8;
    private boolean useBagging = false;
    private int maxDepth = Integer.MAX_VALUE;
    private double minProbability = 1.0;
    private int attributeExcludeDepth = 1;
    private int attributesPerTree = 0;

    public RandomForestBuilder() {
        this(new TreeBuilder());
    }

    public RandomForestBuilder(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    public RandomForestBuilder numTrees(int numTrees) { this.numTrees = numTrees; return this; }
    public RandomForestBuilder useBagging(boolean useBagging) { this.useBagging=useBagging; return this; }
    public RandomForestBuilder maxDepth(int maxDepth) { this.maxDepth=maxDepth; return this; }
    public RandomForestBuilder minProbability(double minProbability) { this.minProbability=minProbability; return this; }
    public RandomForestBuilder attributeExcludeDepth(int depth) { this.attributeExcludeDepth=depth; return this; }
    public RandomForestBuilder attributesPerTree(int attributes) { this.attributesPerTree=attributes; return this; }

    public RandomForest buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        treeBuilder.maxDepth(maxDepth).minProbability(minProbability).attributeExcludeDepth(attributeExcludeDepth);
        List<Tree> trees = Lists.newArrayListWithCapacity(numTrees);

        final AbstractInstance sampleInstance = Iterables.get(trainingData, 0);
        Object[] allAttributes = sampleInstance.getAttributes().keySet().toArray();

        Set<String> excludeAttributes = Sets.newHashSet();
        for (int treeIx = 0; treeIx < numTrees; treeIx++) {
            if(attributesPerTree > 0) {
                excludeAttributes.clear();
                while(excludeAttributes.size() < allAttributes.length-attributesPerTree) {
                    excludeAttributes.add((String) allAttributes[Misc.random.nextInt(allAttributes.length)]);
                }
            }
            Tree tree = treeBuilder.excludeAttributes(excludeAttributes).buildPredictiveModel(trainingData);
            if (attributesPerTree == 0 && tree.node instanceof Branch) {
                Branch branch = (Branch) tree.node;
                excludeAttributes.add(branch.attribute);
            }
            trees.add(tree);
        }

        return new RandomForest(trees);
    }

    private static List<Instance> getBootstrapSampling(Iterable <Instance> trainingData) {
        List<Instance> allInstances = Lists.newArrayList(trainingData);
        List<Instance> sampling = Lists.newArrayList();
        for (int i = 0; i < allInstances.size(); i++) {
            int sample = Misc.random.nextInt(allInstances.size());
            sampling.add(allInstances.get(sample));
        }
        return sampling;
    }

}
