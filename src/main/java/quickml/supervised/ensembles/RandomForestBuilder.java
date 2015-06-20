package quickml.supervised.ensembles;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.Tree;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.branchFinders.TermStatsAndOperations;

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
//what am i promising with generics when i say implements <Classifier, instance type>.  I am promising that the methods of this class will fulfill the contract of what ever uses the generic type.
// meaning, the return types of interface methods can be more specific...but argurment types of interface methods must be written (in the implementer) as exactly what is in the interface

public class RandomForestBuilder<L, I extends InstanceWithAttributesMap<L>, TS extends TermStatsAndOperations<TS>, TR extends Tree, D extends DataProperties> implements PredictiveModelBuilder<RandomDecisionForest<TR>, I> {

    public static final String NUM_TREES = "numTrees";

    private static final Logger logger = LoggerFactory.getLogger(RandomForestBuilder.class);
    private final TreeBuilderHelper<T> treeBuilder;
    private int numTrees = 8;
    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;

    public RandomForestBuilder() {
        this(new TreeBuilderHelper<T>().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7)).minCategoricalAttributeValueOccurances(11).maxDepth(5));
    }

    public RandomForestBuilder(TreeBuilderHelper<T> treeBuilder) {
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
    public RandomDecisionForest buildPredictiveModel(Iterable<T> trainingData) {
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

    private Future<DecisionTree> submitTreeBuild(final Iterable<T> trainingData, final int treeIndex) {
        return executorService.submit(new Callable<DecisionTree>() {
            @Override
            public DecisionTree call() throws Exception {
                return buildModel(trainingData, treeIndex);
            }
        });
    }

    private DecisionTree buildModel(Iterable<T> trainingData, int treeIndex) {
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
