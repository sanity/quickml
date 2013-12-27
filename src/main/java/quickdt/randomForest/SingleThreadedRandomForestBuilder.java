package quickdt.randomForest;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.*;

import java.util.List;
import java.util.Set;

public class SingleThreadedRandomForestBuilder implements PredictiveModelBuilder<RandomForest> {
    private static final  Logger logger =  LoggerFactory.getLogger(RandomForestBuilder.class);

    private final TreeBuilder treeBuilder;
    private int numTrees = 8;
    private boolean useBagging = false;
    private int attributesPerTree = 0;

    public SingleThreadedRandomForestBuilder() {
        this(new TreeBuilder());
    }

    public SingleThreadedRandomForestBuilder(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    public SingleThreadedRandomForestBuilder numTrees(int numTrees) { this.numTrees = numTrees; return this; }
    public SingleThreadedRandomForestBuilder useBagging(boolean useBagging) { this.useBagging=useBagging; return this; }
    public SingleThreadedRandomForestBuilder attributesPerTree(int attributes) { this.attributesPerTree=attributes; return this; }

    public RandomForest buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        logger.info("Building random forest with "+numTrees+" trees, bagging: "+useBagging+", attributes per tree: "+attributesPerTree);
        List<Tree> trees = Lists.newArrayListWithCapacity(numTrees);

        final AbstractInstance sampleInstance = Iterables.get(trainingData, 0);
        Object[] allAttributes = sampleInstance.getAttributes().keySet().toArray();

        Set<String> excludeAttributes = Sets.newHashSet();
        for (int treeIx = 0; treeIx < numTrees; treeIx++) {
            logger.info("Building tree "+treeIx+" of "+numTrees);
            if(attributesPerTree > 0) {
                excludeAttributes.clear();
                while(excludeAttributes.size() < allAttributes.length-attributesPerTree) {
                    excludeAttributes.add((String) allAttributes[Misc.random.nextInt(allAttributes.length)]);
                }
            }
            Tree tree = null;
            treeBuilder.excludeAttributes(excludeAttributes);
            if(useBagging) {
                List<AbstractInstance> sampling = Misc.getBootstrapSampling(trainingData);
                tree = treeBuilder.buildPredictiveModel(sampling);
            } else {
                tree = treeBuilder.buildPredictiveModel(trainingData);
            }
            if (attributesPerTree == 0 && tree.node instanceof Branch) {
                Branch branch = (Branch) tree.node;
                excludeAttributes.add(branch.attribute);
            }
            trees.add(tree);
        }

        return new RandomForest(trees);
    }

}
