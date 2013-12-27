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
  
    // FIXME the problem with the parallelized version of this code is, that the "attributesPerTree" are not considered
    // any longer; essentially, when not using the bagging option, all trees will be identical now. The old
    // implementation had this this code:
  
    //  if (attributesPerTree == 0 && tree.node instanceof Branch) {
    //      Branch branch = (Branch) tree.node;
    //      excludeAttributes.add(branch.attribute);
    //  }
  
    // ... this is missing in the parallelized version now. However, it's not possible to translate this into a
    // parallelized builder.

  // this attribute is not honored any more:
//  private int attributesPerTree = 0;
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

//  public RandomForestBuilder attributesPerTree(int attributes) {
//    this.attributesPerTree = attributes;
//    return this;
//  }

  public RandomForestBuilder executorThreadCount(int threadCount) {
    this.executorThreadCount = threadCount;
    return this;
  }


  public RandomForest buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
    initExecutorService();
    logger.info("Building random forest with " + numTrees + " trees, bagging: " + useBagging /* + ", attributes per tree: " + attributesPerTree */);

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
    logger.info("Building tree " + treeIndex + " of " + numTrees);
    if (useBagging) {
      trainingData = Misc.getBootstrapSampling(trainingData);
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

}
