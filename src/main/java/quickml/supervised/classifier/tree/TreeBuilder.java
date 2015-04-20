package quickml.supervised.classifier.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.*;

public abstract class TreeBuilder<L, I extends InstanceWithAttributesMap<L>, GS extends TermStatistics, TR extends Tree> implements PredictiveModelBuilder<TR, I> {

    private TreeConfig<GS> treeConfig;

    public TreeBuilder(TreeConfig<GS> treeConfig) {
        this.treeConfig = treeConfig.copy();  //defensive copy to preserve thread safety
    }

    public abstract TreeBuilder<L, I, GS, TR> copy();// {returns new TreeBuilder(treeConfig);}


    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeConfig.update(cfg);
    }

    @Override
    public TR buildPredictiveModel(Iterable<I> unprocessedTrainingData) {
        Bagging.TrainingDataPair<L, I> trainingDataPair = prepareTrainingData(unprocessedTrainingData);
        InitializedTreeConfig<GS, TR> initializedTreeConfig = intitialize(trainingDataPair.trainingData);  //make a method a param
        return buildTree(trainingDataPair, initializedTreeConfig);
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

    private TR buildTree(Bagging.TrainingDataPair<L, I> trainingDataPair, InitializedTreeConfig<GS, TR> initializedTreeConfig) {
        Node root = getRootWithDescendants(trainingDataPair.trainingData, initializedTreeConfig);
        TR tree = constructTree(root, initializedTreeConfig);
        return tree;
    }

    private Node getRootWithDescendants(List<I> trainingData, InitializedTreeConfig<GS, TR> initializedTreeConfig) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        //call addNodesAtLevel
        List<Branch> branchesAtPreviousLevel = Lists.newArrayList();
        List<Node> nodesAtLowestLevel;
        GroupsStatsGetter<GS> groupStatisticsGetter = new GroupsStatsGetter<GS>();
        int depth = 0;
        do {
            nodesAtLowestLevel = buildNodesAtNextLevel(branchesAtPreviousLevel, groupStatisticsGetter, initializedTreeConfig); //where do i build the root
            branchesAtPreviousLevel = updateBranchesAtLowestLevel(nodesAtLowestLevel);  // loop through nodesAtLowestLevel and take just the children that we need.
            depth++;
        } while (shouldBuildNextLevel(nodesAtLowestLevel, depth, initializedTreeConfig));
        return TreeBuilderUtils.getRoot(nodesAtLowestLevel.get(0));
    }

    private boolean shouldBuildNextLevel(List<Node> nodesAtLowest, int depth, InitializedTreeConfig<GS, TR> initializedTreeConfig) {
        if (depth > initializedTreeConfig.getTerminationConditions().getMaxDepth()) {
            return false;
        }
        boolean allLeaves =true;
        for (Node node : nodesAtLowest) {
            if (!(node instanceof Leaf)) {
                allLeaves = false;
                break;
            }
        }
        return !allLeaves;
    }

    private List<Node> buildNodesAtNextLevel(List<Branch> branches,  GroupsStatsGetter<GS> groupStatisticsGetter, InitializedTreeConfig<GS, TR> initializedTreeConfig) {
        //each branch should be used by appropriate branch finder.  How was i doing this in the past?
        for (BranchFinder<GS> branchFinder : groupStatisticsGetter.) {
            groupStatisticsGetter.
        }
        if (canTryAddingChildren(parent, train
            ingData))
        {
            return getLeaf(parent, trainingData, initializedTreeConfig);
        }

        Optional<? extends Branch> bestBranchOptional = findBestBranch(parent, trainingData, initializedTreeConfig);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, trainingData, initializedTreeConfig);
        }
        Branch bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = getRootWithDescendants(bestBranch, trueFalsePair.trueTrainingSet, initializedTreeConfig);
        bestBranch.falseChild = getRootWithDescendants(bestBranch, trueFalsePair.falseTrainingSet, initializedTreeConfig);

        return bestBranch;
    }

    private Leaf getLeaf(Branch parent, List<I> trainingData, InitializedTreeConfig<GS> initializedTreeConfig) {
        return initializedTreeConfig.getLeafBuilder().buildLeaf(parent, trainingData);
    }

    private Optional<? extends Branch> findBestBranch(Branch parent, List<I> instances, InitializedTreeConfig<GS, TR> initializedTreeConfig) {

        double bestScore = initializedTreeConfig.getTerminationConditions().getMinScore();
        Optional<? extends Branch> bestBranchOptional = Optional.absent();
        Iterable<BranchFinder<I>> BranchFinders = initializedTreeConfig.getBranchFinders();
        for (BranchFinder<I> BranchFinder : BranchFinders) {
            Optional<? extends Branch> thisBranchOptional = BranchFinder.findBestBranch(parent, instances);
            if (thisBranchOptional.isPresent()) {
                Branch thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {  //minScore evaluation delegated to BranchFinder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    protected TR constructTree(Node node, InitializedTreeConfig<GS, TR> initializedTreeConfig) {
        return initializedTreeConfig.getTreeFactory().constructTree(node);
    }

    protected abstract InitializedTreeConfig<GS, TR> intitialize(List<I> instances);
    /** initializes the tree factory as part of the initialidTreeConfig...note the Treeconfig not a function or Tree.
     *  By having the intitialize here as opposed to TreeConfig, we stop the bleeding of generics into TreeConfig.
     *
     *  So Do have a data properties object here that we put in the tree factory object via it's constructor.
     *  */

}

