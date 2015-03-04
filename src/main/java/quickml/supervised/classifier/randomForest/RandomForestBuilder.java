package quickml.supervised.classifier.randomForest;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomForestBuilder<T extends ClassifierInstance> implements PredictiveModelBuilder<Classifier, T> {

    public static final String NUM_TREES = "numTrees";

    private static final Logger logger = LoggerFactory.getLogger(RandomForestBuilder.class);
    private final TreeBuilder<T> treeBuilder;
    private int numTrees = 8;
    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;

    public RandomForestBuilder() {
        this(new TreeBuilder<T>().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7)).minCategoricalAttributeValueOccurances(11).maxDepth(16));
    }

    public RandomForestBuilder(TreeBuilder<T> treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    @Override
    public void updateBuilderConfig(Map<String, Object> config) {
        treeBuilder.updateBuilderConfig(config);
        if (config.containsKey(NUM_TREES))
            this.numTrees((Integer) config.get(NUM_TREES));
    }

    public RandomForestBuilder<T> numTrees(int numTrees) {
        this.numTrees = numTrees;
        return this;
    }

    public RandomForestBuilder<T> executorThreadCount(int threadCount) {
        this.executorThreadCount = threadCount;
        return this;
    }

    @Override
    public RandomForest buildPredictiveModel(Iterable<T> trainingData) {
        executorService = Executors.newFixedThreadPool(executorThreadCount);
        logger.info("Building random forest with {} trees", numTrees);

        List<Future<Tree>> treeFutures = Lists.newArrayListWithCapacity(numTrees);
        List<Tree> trees = Lists.newArrayListWithCapacity(numTrees);

        // Submit all tree building jobs to the executor
        for (int treeIndex = 0; treeIndex < numTrees; treeIndex++) {
            treeFutures.add(submitTreeBuild(trainingData, treeIndex));
        }

        // Collect all completed trees. Will block until complete
        collectTreeFutures(trees, treeFutures);
        Set<Serializable> classifications = new HashSet<>();
        for (Tree tree : trees) {
            classifications.addAll(tree.getClassifications());
        }
        return new RandomForest(trees, classifications);
    }

    private Future<Tree> submitTreeBuild(final Iterable<T> trainingData, final int treeIndex) {
        return executorService.submit(new Callable<Tree>() {
            @Override
            public Tree call() throws Exception {
                return buildModel(trainingData, treeIndex);
            }
        });
    }

    private Tree buildModel(Iterable<T> trainingData, int treeIndex) {
        logger.debug("Building tree {} of {}", treeIndex, numTrees);
        return treeBuilder.copy().buildPredictiveModel(trainingData);
    }


    protected void collectTreeFutures(List<Tree> trees, List<Future<Tree>> treeFutures) {
        for (Future<Tree> treeFuture : treeFutures) {
            collectTreeFutures(trees, treeFuture);
        }

        executorService.shutdown();
    }

    private void collectTreeFutures(List<Tree> trees, Future<Tree> treeFuture) {
        try {
            trees.add(treeFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
