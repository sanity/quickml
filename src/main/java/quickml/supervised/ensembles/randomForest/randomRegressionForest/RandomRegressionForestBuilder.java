package quickml.supervised.ensembles.randomForest.randomRegressionForest;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.PredictionMap;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.ensembles.randomForest.RandomForestBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import quickml.supervised.tree.regressionTree.RegressionTree;
import quickml.supervised.tree.regressionTree.RegressionTreeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static quickml.supervised.tree.constants.ForestOptions.NUM_TREES;


public class RandomRegressionForestBuilder<I extends RegressionInstance> extends RandomForestBuilder<Double, RandomRegressionForest, I> {


    //TODO: copy treeBuilder before submitting
    private static final Logger logger = LoggerFactory.getLogger(RandomRegressionForestBuilder.class);
    private final RegressionTreeBuilder<I> treeBuilder;
    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;

    public RandomRegressionForestBuilder() {
        this(new RegressionTreeBuilder<I>().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7)).maxDepth(5));
    }

    public RandomRegressionForestBuilder(RegressionTreeBuilder<I> treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        treeBuilder.updateBuilderConfig(config);
        if (config.containsKey(NUM_TREES.name()))
            this.numTrees((Integer) config.get(NUM_TREES.name()));
    }

    public RandomRegressionForestBuilder<I> numTrees(int numTrees) {
        super.numTrees = numTrees;
        return this;
    }

    public RandomRegressionForestBuilder<I> executorThreadCount(int threadCount) {
        this.executorThreadCount = threadCount;
        return this;
    }

    @Override
    public RandomRegressionForest buildPredictiveModel(Iterable<I> trainingData) {
        executorService = Executors.newFixedThreadPool(executorThreadCount);
        logger.info("Building random forest with {} trees", numTrees);

        List<Future<RegressionTree>> treeFutures = Lists.newArrayListWithCapacity(numTrees);
        List<RegressionTree> regressionTrees = Lists.newArrayListWithCapacity(numTrees);

        // Submit all oldTree building jobs to the executor
        for (int treeIndex = 0; treeIndex < numTrees; treeIndex++) {
            treeFutures.add(submitTreeBuild(trainingData, treeIndex));
        }

        // Collect all completed trees. Will block until complete
        collectTreeFutures(regressionTrees, treeFutures);

        return new RandomRegressionForest(regressionTrees);
    }

    private Future<RegressionTree> submitTreeBuild(final Iterable<I> trainingData, final int treeIndex) {
        return executorService.submit(new Callable<RegressionTree>() {
            @Override
            public RegressionTree call() throws Exception {
                return buildModel(trainingData, treeIndex);
            }
        });
    }

    private RegressionTree buildModel(Iterable<I> trainingData, int treeIndex) {
        logger.debug("Building oldTree {} of {}", treeIndex, numTrees);
        return treeBuilder.copy().buildPredictiveModel(trainingData);
    }


    protected void collectTreeFutures(List<RegressionTree> regressionTrees, List<Future<RegressionTree>> treeFutures) {
        for (Future<RegressionTree> treeFuture : treeFutures) {
            collectTreeFutures(regressionTrees, treeFuture);
        }
        executorService.shutdown();
    }

    private void collectTreeFutures(List<RegressionTree> regressionTrees, Future<RegressionTree> treeFuture) {
        try {
            regressionTrees.add(treeFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
