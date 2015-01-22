package quickml.supervised.classifier.randomForest;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
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
public class RandomForestBuilder implements PredictiveModelBuilder<AttributesMap, RandomForest> {
    private static final Logger logger = LoggerFactory.getLogger(RandomForestBuilder.class);
    private final TreeBuilder treeBuilder;
    private int numTrees = 20;
    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;

    public RandomForestBuilder() {
        this(new TreeBuilder().ignoreAttributeAtNodeProbability(0.5));
    }

    public RandomForestBuilder(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    public RandomForestBuilder numTrees(int numTrees) {
        this.numTrees = numTrees;
        return this;
    }

    public RandomForestBuilder executorThreadCount(int threadCount) {
        this.executorThreadCount = threadCount;
        return this;
    }

    @Override
    public synchronized RandomForest buildPredictiveModel(final Iterable<? extends Instance<AttributesMap>> trainingData) {
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


    private Future<Tree> submitTreeBuild(final Iterable<? extends Instance<AttributesMap>> trainingData, final int treeIndex) {
        return executorService.submit(new Callable<Tree>() {
            @Override
            public Tree call() throws Exception {
                return buildModel(trainingData, treeIndex);
            }
        });
    }

    private Tree buildModel(Iterable<? extends Instance<AttributesMap>> trainingData, int treeIndex) {
        logger.debug("Building tree {} of {}", treeIndex, numTrees);
        return treeBuilder.buildPredictiveModel(trainingData);
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
            throw new RuntimeException(e);
        }
    }
}
