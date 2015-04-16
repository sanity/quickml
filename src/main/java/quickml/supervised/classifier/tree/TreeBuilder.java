package quickml.supervised.classifier.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.*;

public abstract class TreeBuilder<L, I extends InstanceWithAttributesMap<L>, GS extends GroupStatistics, TC extends TreeConfig<L, I, GS, TC>,  ITC extends InitializedTreeConfig<L, I, GS>> implements PredictiveModelBuilder<Tree, I> {
//ok to make Tree a generic

    private TC treeConfig;

    public TreeBuilder(TC treeConfig) {
        this.treeConfig = treeConfig.copy();  //defensive copy to preserve thread safety
    }

    public abstract TreeBuilder<L, I, GS, TC, ITC> copy();// {returns new TreeBuilder(treeConfig);}


    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeConfig.update(cfg);
    }

    @Override
    public Tree buildPredictiveModel(Iterable<I> unprocessedTrainingData) {
        Bagging.TrainingDataPair<I> trainingDataPair = prepareTrainingData(unprocessedTrainingData);
        ITC initializedTreeConfig = treeConfig.buildForestConfig(trainingDataPair.trainingData);  //make a method a param
        return buildTree(trainingDataPair, initializedTreeConfig);
    }

    private Bagging.TrainingDataPair<I> prepareTrainingData(Iterable<I> unprocessedTrainingData) {
        List<I> trainingData = Lists.newArrayList(unprocessedTrainingData);
        Bagging.TrainingDataPair<I> trainingDataPair = new Bagging.TrainingDataPair<>(trainingData, null);
        if (treeConfig.getBagging().isPresent()) {
            Bagging<I> bagging = treeConfig.getBagging().get();
            trainingDataPair = bagging.separateTrainingDataFromOutOfBagData(trainingData);
        }
        return trainingDataPair;
    }

    private Tree buildTree(Bagging.TrainingDataPair<I> trainingDataPair, ITC initializedTreeConfig) {
        Branch parent = null; //parent of root should be null
        Node root = createNode(parent, trainingDataPair.trainingData, initializedTreeConfig);
        Tree tree = constructTree(root, initializedTreeConfig);
        if (canPrune(trainingDataPair.outOfBagTrainingData, initializedTreeConfig)) {
            tree = initializedTreeConfig.getPostPruningStrategy().get().prune(tree, trainingDataPair.outOfBagTrainingData.get());
        }
        return tree;
    }

    protected abstract Tree constructTree(Node node, ITC initializedTreeConfig);  //, initializedTreeConfig.getDataProperities()

    protected  abstract boolean canTryAddingChildren(Branch parent, List<I> trainingData);

    private boolean canPrune(Optional<List<I>> outOfBagTrainingData, ITC initializedTreeConfig) {
        return initializedTreeConfig.getPostPruningStrategy().isPresent() && outOfBagTrainingData.isPresent();
    }

    private Node createNode(Branch parent, List<I> trainingData, ITC initializedTreeConfig) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        if (canTryAddingChildren(parent, trainingData))
        {
            return getLeaf(parent, trainingData, initializedTreeConfig);
        }

        Optional<? extends Branch> bestBranchOptional = findBestBranch(parent, trainingData, initializedTreeConfig);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, trainingData, initializedTreeConfig);
        }
        Branch bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet, initializedTreeConfig);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet, initializedTreeConfig);

        return bestBranch;
    }

    private Leaf getLeaf(Branch parent, List<I> trainingData, ITC initializedTreeConfig) {
        return initializedTreeConfig.getLeafBuilder().buildLeaf(parent, trainingData);
    }

    private Optional<? extends Branch> findBestBranch(Branch parent, List<I> instances, ITC initializedTreeConfig) {

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
}

