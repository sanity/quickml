package quickdt.randomForest;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.*;

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
  private int numTrees = 8;
  private boolean useBagging = false;
  private int executorThreadCount = 8;
  private ExecutorService executorService;

  public RandomForestBuilder() {
    this(new TreeBuilder());
  }

  public RandomForestBuilder(TreeBuilder treeBuilder) {
    this.treeBuilder = treeBuilder;
  }

  public RandomForestBuilder numTrees(int numTrees) {
    this.numTrees = numTrees;
    return this;
  }

  public RandomForestBuilder useBagging(boolean useBagging) {
    this.useBagging = useBagging;
    return this;
  }

  public RandomForestBuilder executorThreadCount(int threadCount) {
    this.executorThreadCount = threadCount;
    return this;
  }


  public RandomForest buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
    initExecutorService();
    logger.info("Building random forest with {} trees, bagging {}", numTrees, useBagging);

    List<Future<Tree>> treeFutures = Lists.newArrayListWithCapacity(numTrees);
    List<Tree> trees = Lists.newArrayListWithCapacity(numTrees);

    // Submit all tree building jobs to the executor
    for (int idx = 0; idx < numTrees; idx++) {
      final int treeIndex = idx;
      treeFutures.add(submitTreeBuild(trainingData, treeIndex));
    }

    // Collect all completed trees. Will block until complete
    for (Future<Tree> treeFuture : treeFutures) {
      collectTreeFutures(trees, treeFuture);
    }
    
    executorService.shutdown();

    return new RandomForest(trees);
  }

  private Future<Tree> submitTreeBuild(final Iterable<? extends AbstractInstance> trainingData, final int treeIndex) {
    return executorService.submit(new Callable<Tree>() {
      @Override
      public Tree call() throws Exception {
        return buildModel(trainingData, treeIndex);
      }
    });
  }

  private void initExecutorService() {
    if (executorService == null) {
      executorService = Executors.newFixedThreadPool(executorThreadCount);
    }
  }

  private Tree buildModel(Iterable<? extends AbstractInstance> trainingData, int treeIndex) {
    logger.info("Building tree {} of {}", treeIndex, numTrees);
    if (useBagging) {
      trainingData = getBootstrapSampling(trainingData);
    }
    return treeBuilder.buildPredictiveModel(trainingData);
  }

  private void collectTreeFutures(List<Tree> trees, Future<Tree> treeFuture) {
    try {
      trees.add(treeFuture.get());
    } catch (Exception e) {
      logger.error("Error retrieving tree", e);
    }
  }

  /**
   * <p>
   * Simple implementation of a bagging predictor using multiple decision trees.
   * The idea is to create a random bootstrap sample of the training data to grow
   * multiple trees. For more information see <a
   * href="http://www.stat.berkeley.edu/tech-reports/421.pdf">Bagging
   * Predictors</a>, Leo Breiman, 1994.
   * </p>
   * <p/>
   * Bagging code taken from contribution by Philipp Katz
   */
  private static List<AbstractInstance> getBootstrapSampling(Iterable<? extends AbstractInstance> trainingData) {
    final List<? extends AbstractInstance> allInstances = Lists.newArrayList(trainingData);
    final List<AbstractInstance> sampling = Lists.newArrayList();
    for (int i = 0; i < allInstances.size(); i++) {
      int sample = Misc.random.nextInt(allInstances.size());
      sampling.add(allInstances.get(sample));
    }
    return sampling;
  }

}
