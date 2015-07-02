package quickml.supervised.ensembles.randomForest.randomDecisionForest;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.ClassifierInstance;
import quickml.data.PredictionMap;
import quickml.supervised.ensembles.randomForest.RandomForestBuilder;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static  quickml.supervised.tree.constants.ForestOptions.*;


public class RandomDecisionForestBuilder<I extends ClassifierInstance> extends RandomForestBuilder<PredictionMap, RandomDecisionForest, I> {


    //TODO: copy treeBuilder before submitting
    private static final Logger logger = LoggerFactory.getLogger(RandomDecisionForestBuilder.class);
    private final DecisionTreeBuilder<I> treeBuilder;
    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;

    public RandomDecisionForestBuilder() {
        this(new DecisionTreeBuilder<I>().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7)).maxDepth(5));
    }

    public RandomDecisionForestBuilder(DecisionTreeBuilder<I> treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        treeBuilder.updateBuilderConfig(config);
        if (config.containsKey(NUM_TREES.name()))
            this.numTrees((Integer) config.get(NUM_TREES));
    }

    public RandomDecisionForestBuilder<I> numTrees(int numTrees) {
        super.numTrees = numTrees;
        return this;
    }

    public RandomDecisionForestBuilder<I> executorThreadCount(int threadCount) {
        this.executorThreadCount = threadCount;
        return this;
    }

    @Override
    public RandomDecisionForest buildPredictiveModel(Iterable<I> trainingData) {
        executorService = Executors.newFixedThreadPool(executorThreadCount);
        logger.info("Building random forest with {} trees", numTrees);

        List<Future<DecisionTree>> treeFutures = Lists.newArrayListWithCapacity(numTrees);
        List<DecisionTree> decisionTrees = Lists.newArrayListWithCapacity(numTrees);

        // Submit all tree building jobs to the executor
        for (int treeIndex = 0; treeIndex < numTrees; treeIndex++) {
            treeFutures.add(submitTreeBuild(trainingData, treeIndex));
        }

        // Collect all completed trees. Will block until complete
        collectTreeFutures(decisionTrees, treeFutures);
        Set<Serializable> classifications = new HashSet<>();
        for (DecisionTree decisionTree : decisionTrees) {
            classifications.addAll(decisionTree.getClassifications());
        }
        return new RandomDecisionForest(decisionTrees, classifications);
    }

    private Future<DecisionTree> submitTreeBuild(final Iterable<I> trainingData, final int treeIndex) {
        return executorService.submit(new Callable<DecisionTree>() {
            @Override
            public DecisionTree call() throws Exception {
                return buildModel(trainingData, treeIndex);
            }
        });
    }

    private DecisionTree buildModel(Iterable<I> trainingData, int treeIndex) {
        logger.debug("Building tree {} of {}", treeIndex, numTrees);
        return treeBuilder.copy().buildPredictiveModel(trainingData);
    }


    protected void collectTreeFutures(List<DecisionTree> decisionTrees, List<Future<DecisionTree>> treeFutures) {
        for (Future<DecisionTree> treeFuture : treeFutures) {
            collectTreeFutures(decisionTrees, treeFuture);
        }
        executorService.shutdown();
    }

    private void collectTreeFutures(List<DecisionTree> decisionTrees, Future<DecisionTree> treeFuture) {
        try {
            decisionTrees.add(treeFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
