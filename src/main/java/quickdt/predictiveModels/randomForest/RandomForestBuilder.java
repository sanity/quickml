package quickdt.predictiveModels.randomForest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Misc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomForestBuilder implements PredictiveModelBuilder<RandomForest> {
  private static final Logger logger = LoggerFactory.getLogger(RandomForestBuilder.class);
  private final TreeBuilder treeBuilder;
  private int numTrees = 20;
  private int executorThreadCount = Runtime.getRuntime().availableProcessors();
  private ExecutorService executorService;
  private int baggingSampleSize = 0;

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

    /**
     * Setting this to a value greater than zero will turn on bagging (see
     * <a href="http://en.wikipedia.org/wiki/Bootstrap_aggregating">Bootstrap aggregating</a>.
     * Use Integer.MAX_VALUE to set the bag size to be the same as the training set size.
     *
     * @param sampleSize The size of each bag, 0 to deactivate bagging (defaults to 0).  Will use
     *                   the smaller of this value and the training set size.
     * @return
     */
  public RandomForestBuilder withBagging(int sampleSize) {
      Preconditions.checkArgument(sampleSize > -1, "Sample size must not be negative");
      this.baggingSampleSize = sampleSize;
      return this;
  }

  public RandomForestBuilder executorThreadCount(int threadCount) {
    this.executorThreadCount = threadCount;
    return this;
  }


  public RandomForest buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
    executorService = Executors.newFixedThreadPool(executorThreadCount);
    logger.info("Building random forest with {} trees", numTrees);

    List<Future<Tree>> treeFutures = Lists.newArrayListWithCapacity(numTrees);
    List<Tree> trees = Lists.newArrayListWithCapacity(numTrees);

    // Submit all tree building jobs to the executor
    for (int treeIndex = 0; treeIndex < numTrees; treeIndex++) {
      Iterable<? extends AbstractInstance> treeTrainingData = shuffleTrainingData(trainingData);
      treeFutures.add(submitTreeBuild(treeTrainingData, treeIndex));
    }

    // Collect all completed trees. Will block until complete
    for (Future<Tree> treeFuture : treeFutures) {
      collectTreeFutures(trees, treeFuture);
    }
    
    executorService.shutdown();

    return new RandomForest(trees);
  }

    private Iterable<? extends AbstractInstance> shuffleTrainingData(Iterable<? extends AbstractInstance> trainingData) {
        Iterable<? extends AbstractInstance> treeTrainingData;
        final int bagSize = Math.min(Iterables.size(trainingData), baggingSampleSize);
        if (bagSize > 0) {
          ArrayList<AbstractInstance> treeTrainingDataArrayList = Lists.newArrayListWithExpectedSize(bagSize);
          for (AbstractInstance instance : Iterables.limit(trainingData, bagSize)) {
              treeTrainingDataArrayList.add(instance);
          }
          for (AbstractInstance instance : trainingData) {
              int position = Misc.random.nextInt(bagSize);
              treeTrainingDataArrayList.add(position, instance);
          }
          treeTrainingData = treeTrainingDataArrayList;
      } else {
          treeTrainingData = trainingData;
      }
        return treeTrainingData;
    }

    public void updatePredictiveModel(PredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingData) {
        executorService = Executors.newFixedThreadPool(executorThreadCount);
        RandomForest randomForest = (RandomForest) predictiveModel;
        List<Future<Tree>> treeFutures = Lists.newArrayListWithCapacity(randomForest.trees.size());
        int treeIndex = 0;
        for(Tree tree : randomForest.trees) {
            Iterable<? extends AbstractInstance> treeTrainingData = shuffleTrainingData(trainingData);
            treeFutures.add(submitTreeUpdate(tree, treeTrainingData, treeIndex));
            treeIndex++;
        }

        List<Tree> trees = Lists.newArrayListWithCapacity(randomForest.trees.size());

        // Collect all completed trees. Will block until complete
        for (Future<Tree> treeFuture : treeFutures) {
            collectTreeFutures(trees, treeFuture);
        }
    }

  private Future<Tree> submitTreeBuild(final Iterable<? extends AbstractInstance> trainingData, final int treeIndex) {
    return executorService.submit(new Callable<Tree>() {
      @Override
      public Tree call() throws Exception {
        return buildModel(trainingData, treeIndex);
      }
    });
  }

  private Future<Tree> submitTreeUpdate(final Tree tree, final Iterable<? extends AbstractInstance> trainingData, final int treeIndex) {
      return executorService.submit(new Callable<Tree>() {
          @Override
          public Tree call() throws Exception {
              return updateModel(tree, trainingData, treeIndex);
          }
      });
  }

  private Tree buildModel(Iterable<? extends AbstractInstance> trainingData, int treeIndex) {
    logger.info("Building tree {} of {}", treeIndex, numTrees);
    return treeBuilder.buildPredictiveModel(trainingData);
  }

  private Tree updateModel(Tree tree, final Iterable<? extends AbstractInstance> trainingData, final int treeIndex) {
      logger.info("Updating tree {} of {}", treeIndex, numTrees);
      treeBuilder.updatePredictiveModel(tree, trainingData);
      return tree;
  }

  private void collectTreeFutures(List<Tree> trees, Future<Tree> treeFuture) {
    try {
      trees.add(treeFuture.get());
    } catch (Exception e) {
      logger.error("Error retrieving tree", e);
    }
  }
}
