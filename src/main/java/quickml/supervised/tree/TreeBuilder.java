package quickml.supervised.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchSplitStatistics.AggregateStatistics;
import quickml.supervised.tree.branchSplitStatistics.InstancesToAttributeStatistics;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.nodes.ParentOfRoot;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.terminationConditions.TerminationConditions;
import quickml.supervised.tree.configurations.InitializedTreeConfig;
import quickml.supervised.tree.configurations.TreeConfig;
import quickml.supervised.tree.configurations.TreeConfigInitializer;

import java.util.*;

public abstract class TreeBuilder<L, P,  I extends InstanceWithAttributesMap<L>, TS extends TermStatsAndOperations<TS>, TR extends Tree<P>, D extends DataProperties> implements PredictiveModelBuilder<TR, I> {

    protected TreeConfig<TS, D> treeConfig;
    protected TreeConfigInitializer<L, I, TS, D> treeConfigInitializer;
    protected AggregateStatistics<L, I, TS> aggregateStatistics;

    protected TreeBuilder(TreeConfig<TS, D> treeConfig, TreeConfigInitializer<L, I, TS, D> treeConfigInitializer, AggregateStatistics<L, I, TS> aggregateStatistics) {
        this.treeConfig = treeConfig.copy();
        this.treeConfigInitializer = treeConfigInitializer; //abstract factory pattern (applied to all public (stateless wrt training data) fields)
        this.aggregateStatistics = aggregateStatistics;// abstract factory pattern
    }

    @Override
    public TR buildPredictiveModel(Iterable<I> unprocessedTrainingData) {
        Bagging.TrainingDataPair<L, I> trainingDataPair = prepareTrainingData(unprocessedTrainingData);
        InitializedTreeConfig<TS, D> initializedTreeConfig = treeConfigInitializer.createTreeConfig(trainingDataPair.trainingData, treeConfig);  //make a method a param
        Map<BranchType, InstancesToAttributeStatistics<L, I, TS>> instancesToAttributeStatisticsMap = initializeInstancesToAttributeStatistics(initializedTreeConfig);
        return buildTree(trainingDataPair, initializedTreeConfig, instancesToAttributeStatisticsMap);
    }

    private Bagging.TrainingDataPair<L, I> prepareTrainingData(Iterable<I> unprocessedTrainingData) {
        List<I> trainingData = Lists.newArrayList(unprocessedTrainingData);
        Bagging.TrainingDataPair<L, I> trainingDataPair = new Bagging.TrainingDataPair<>(trainingData, null);
        if (treeConfig.getBagging().isPresent()) {
            Bagging bagging = treeConfig.getBagging().get();
            trainingDataPair = bagging.separateTrainingDataFromOutOfBagData(trainingData);
        }
        return trainingDataPair;
    }

    private TR buildTree(Bagging.TrainingDataPair<L, I> trainingDataPair, InitializedTreeConfig<TS, D> initializedTreeConfig, Map<BranchType, InstancesToAttributeStatistics<L, I, TS>> instancesToAttributeStatisticsMap) {
        Node root = createNode(new ParentOfRoot<TS>(), trainingDataPair.trainingData, initializedTreeConfig, instancesToAttributeStatisticsMap);
        TR tree = constructTree(root, initializedTreeConfig.getDataProperties());
        return tree;
    }

    private Node<TS> createNode(Branch<TS> parent, List<I> trainingData, InitializedTreeConfig<TS, D> initializedTreeConfig, Map<BranchType, InstancesToAttributeStatistics<L, I, TS>> instancesToAttributeStatisticsMap) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        TerminationConditions<TS> terminationConditions = initializedTreeConfig.getTerminationConditions();
        TS aggregateStats = getTermStats(parent, trainingData);
        if (terminationConditions.canTryAddingChildren(parent, aggregateStats)) {
            return getLeaf(parent, aggregateStats, initializedTreeConfig);
        }
        Optional<? extends Branch<TS>> bestBranchOptional = findBestBranch(parent, trainingData, initializedTreeConfig, instancesToAttributeStatisticsMap);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, aggregateStats, initializedTreeConfig);
        }
        Branch<TS> bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet, initializedTreeConfig, instancesToAttributeStatisticsMap);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet, initializedTreeConfig, instancesToAttributeStatisticsMap);

        return bestBranch;
    }

    private Optional<? extends Branch<TS>> findBestBranch(Branch parent, List<I> instances, InitializedTreeConfig<TS, D> initializedTreeConfig, Map<BranchType, InstancesToAttributeStatistics<L, I, TS>> instancesToAttributeStatisticsMap) {

        double bestScore = initializedTreeConfig.getTerminationConditions().getMinScore();
        Optional<? extends Branch<TS>> bestBranchOptional = Optional.absent();
        Iterable<BranchFinder<TS>> branchFinders = initializedTreeConfig.getBranchFinders();
        for (BranchFinder<TS> branchFinder : branchFinders) {
            InstancesToAttributeStatistics<L, I, TS> instancesToAttributeStatistics = instancesToAttributeStatisticsMap.get(branchFinder.getBranchType());
            Optional<? extends Branch<TS>> thisBranchOptional = branchFinder.findBestBranch(parent, instancesToAttributeStatistics); //decoupling occurs bc instancesToAttributeStatistics implements a simpler interface
            if (thisBranchOptional.isPresent()) {
                Branch<TS> thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {  //minScore evaluation delegated to branchFinder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private TS getTermStats(Branch<TS> parent, List<I> trainingData) {
        return !parent.isEmpty() ? parent.termStatistics : aggregateStatistics.getAggregateStats(trainingData);
    }

    private Leaf<TS> getLeaf(Branch<TS> parent, TS aggregateStats, InitializedTreeConfig<TS, D> initializedTreeConfig) {
        return initializedTreeConfig.getLeafBuilder().buildLeaf(parent, aggregateStats);
    }

    protected abstract TR constructTree(Node<TS> node, D dataProperties); //factory method pattern

    protected abstract Map<BranchType, InstancesToAttributeStatistics<L, I, TS>> initializeInstancesToAttributeStatistics(InitializedTreeConfig<TS, D> initializedTreeConfig); //factory method pattern

    public abstract TreeBuilder<L, P, I, TS, TR, D> copy();// {returns new TreeBuilder(configurations);}


    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeConfig.update(cfg);
    }


}

