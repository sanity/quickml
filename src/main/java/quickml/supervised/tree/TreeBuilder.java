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
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
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

public abstract class TreeBuilder<L, P,  I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, TR extends Tree<P>, D extends DataProperties> implements PredictiveModelBuilder<TR, I> {

    protected TreeConfig<VC, D> treeConfig;
    protected TreeConfigInitializer<L, I, VC, D> treeConfigInitializer;
    protected AggregateStatistics<L, I, VC> aggregateStatistics;

    protected TreeBuilder(TreeConfig<VC, D> treeConfig, TreeConfigInitializer<L, I, VC, D> treeConfigInitializer, AggregateStatistics<L, I, VC> aggregateStatistics) {
        this.treeConfig = treeConfig.copy();
        this.treeConfigInitializer = treeConfigInitializer; //abstract factory pattern (applied to all public (stateless wrt training data) fields)
        this.aggregateStatistics = aggregateStatistics;// abstract factory pattern
    }

    @Override
    public TR buildPredictiveModel(Iterable<I> unprocessedTrainingData) {
        Bagging.TrainingDataPair<L, I> trainingDataPair = prepareTrainingData(unprocessedTrainingData);
        InitializedTreeConfig<VC, D> initializedTreeConfig = treeConfigInitializer.createTreeConfig(trainingDataPair.trainingData, treeConfig);  //make a method a param
        Map<BranchType, InstancesToAttributeStatistics<L, I, VC>> instancesToAttributeStatisticsMap = initializeInstancesToAttributeStatistics(initializedTreeConfig);
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

    private TR buildTree(Bagging.TrainingDataPair<L, I> trainingDataPair, InitializedTreeConfig<VC, D> initializedTreeConfig, Map<BranchType, InstancesToAttributeStatistics<L, I, VC>> instancesToAttributeStatisticsMap) {
        Node root = createNode(new ParentOfRoot<VC>(), trainingDataPair.trainingData, initializedTreeConfig, instancesToAttributeStatisticsMap);
        TR tree = constructTree(root, initializedTreeConfig.getDataProperties());
        return tree;
    }

    private Node<VC> createNode(Branch<VC> parent, List<I> trainingData, InitializedTreeConfig<VC, D> initializedTreeConfig, Map<BranchType, InstancesToAttributeStatistics<L, I, VC>> instancesToAttributeStatisticsMap) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        TerminationConditions<VC> terminationConditions = initializedTreeConfig.getTerminationConditions();
        VC aggregateStats = getTermStats(parent, trainingData);
        if (terminationConditions.canTryAddingChildren(parent, aggregateStats)) {
            return getLeaf(parent, aggregateStats, initializedTreeConfig);
        }
        Optional<? extends Branch<VC>> bestBranchOptional = findBestBranch(parent, trainingData, initializedTreeConfig, instancesToAttributeStatisticsMap);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, aggregateStats, initializedTreeConfig);
        }
        Branch<VC> bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet, initializedTreeConfig, instancesToAttributeStatisticsMap);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet, initializedTreeConfig, instancesToAttributeStatisticsMap);

        return bestBranch;
    }

    private Optional<? extends Branch<VC>> findBestBranch(Branch parent, List<I> instances, InitializedTreeConfig<VC, D> initializedTreeConfig, Map<BranchType, InstancesToAttributeStatistics<L, I, VC>> instancesToAttributeStatisticsMap) {

        double bestScore = initializedTreeConfig.getTerminationConditions().getMinScore();
        Optional<? extends Branch<VC>> bestBranchOptional = Optional.absent();
        Iterable<BranchFinder<VC>> branchFinders = initializedTreeConfig.getBranchFinders();
        for (BranchFinder<VC> branchFinder : branchFinders) {
            InstancesToAttributeStatistics<L, I, VC> instancesToAttributeStatistics = instancesToAttributeStatisticsMap.get(branchFinder.getBranchType());
            instancesToAttributeStatistics.setTrainingData(instances);
            Optional<? extends Branch<VC>> thisBranchOptional = branchFinder.findBestBranch(parent, instancesToAttributeStatistics); //decoupling occurs bc instancesToAttributeStatistics implements a simpler interface
            if (thisBranchOptional.isPresent()) {
                Branch<VC> thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {  //minScore evaluation delegated to branchFinder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private VC getTermStats(Branch<VC> parent, List<I> trainingData) {
        return !parent.isEmpty() ? parent.termStatistics : aggregateStatistics.getAggregateStats(trainingData);
    }

    private Leaf<VC> getLeaf(Branch<VC> parent, VC aggregateStats, InitializedTreeConfig<VC, D> initializedTreeConfig) {
        return initializedTreeConfig.getLeafBuilder().buildLeaf(parent, aggregateStats);
    }
    //replacing Node<TS>  with Nd where Nd extends Node<TS>
    protected abstract TR constructTree(Node<VC> node, D dataProperties); //factory method pattern

    protected abstract Map<BranchType, InstancesToAttributeStatistics<L, I, VC>> initializeInstancesToAttributeStatistics(InitializedTreeConfig<VC, D> initializedTreeConfig); //factory method pattern

    public abstract TreeBuilder<L, P, I, VC, TR, D> copy();// {returns new TreeBuilder(configurations);}


    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeConfig.update(cfg);
    }


}

