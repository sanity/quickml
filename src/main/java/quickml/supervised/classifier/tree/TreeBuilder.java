package quickml.supervised.classifier.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.*;

public  class TreeBuilder<T extends InstanceWithAttributesMap, S extends SplitProperties, D extends DataProperties> implements PredictiveModelBuilder<Tree, T> {
//TO
    private TreeConfig<T, S, D> configBuilder;
    private InitializedTreeConfig<T, S, D> initializedTreeConfig;
    private Optional<List<T>> outOfBagTrainingData = Optional.absent();  //make non field...and an empty list by default


    public TreeBuilder(TreeConfig treeConfig) {
        this.configBuilder = treeConfig.copy();  //defensive copy to preserve thread safety
    }

    public TreeBuilder<T, S, D> copy() {
        return new TreeBuilder(configBuilder.copy());
    }

    public void updateBuilderConfig(Map<String, Object> cfg) {
        configBuilder.update(cfg);
    }

    @Override
    public Tree buildPredictiveModel(Iterable<T> unprocessedTrainingData) {
        List<T> trainingData = prepareTrainingData(unprocessedTrainingData);
        initializedTreeConfig = configBuilder.buildForestConfig(trainingData);
        return buildTree(trainingData);

    }

    //forest config should have a bagging object, since there can be more than one type of bagging
    private List<T> prepareTrainingData(Iterable<T> unprocessedTrainingData) {
        List<T> trainingData = Utils.<T>iterableToList(unprocessedTrainingData);
        if (initializedTreeConfig.getBagging().isPresent()) {
            Bagging<T> bagging = initializedTreeConfig.getBagging().get();
            Bagging.BaggedPair<T> baggedPair = bagging.separateTrainingDataFromOutOfBagData(trainingData);
            this.outOfBagTrainingData = Optional.of(baggedPair.outOfBagTrainingData);
            trainingData = baggedPair.baggedTrainingData;
        }
        return trainingData;
    }

    private Tree buildTree(List<T> trainingData) {
        Branch parent = null; //parent of root should be null
        Node root = createNode(parent, trainingData);
        TreeFactory treeFactory = initializedTreeConfig.getTreeFactory(); //forest config should have method
        Tree tree = treeFactory.constructTree(root, initializedTreeConfig.getDataProperities());
        if (canPrune()) {
            tree = initializedTreeConfig.getPostPruningStrategy().get().prune(tree, outOfBagTrainingData.get());
        }
        return tree;
    }
//if use inheritance: make a build tree method which is implented in specific treebuilders.  Tree would need to be generic as well...and it's ok cuz no one will have handle on the generic


    private boolean canPrune() {
        return initializedTreeConfig.getPostPruningStrategy().isPresent() && outOfBagTrainingData.isPresent();
    }

    private Node createNode(Branch parent, List<T> trainingData) {
        Preconditions.checkArgument(trainingData == null || trainingData.size() ==0, "Can't build a tree with no training data");
        TerminationConditions<S> terminationConditions = initializedTreeConfig.getTerminationConditions();
        if (!terminationConditions.canTryAddingChildren(parent, trainingData)) {
            return getLeaf(parent, trainingData);
        }

        Optional<? extends Branch> bestBranchOptional = findBestBranch(parent, trainingData);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, trainingData);
        }
        Branch bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<T> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet);

        return bestBranch;
    }

    private Leaf getLeaf(Branch parent, List<T> trainingData) {
        return initializedTreeConfig.getLeafBuilder().buildLeaf(parent, trainingData);
    }

    private Optional<? extends Branch> findBestBranch(Branch parent, List<T> instances) {

        double bestScore = initializedTreeConfig.getTerminationConditions().getMinScore();
        Optional<? extends Branch> bestBranchOptional = Optional.absent();
        Iterable<BranchFinder<T>> BranchFinders = initializedTreeConfig.getBranchFinders();
        for (BranchFinder<T> BranchFinder : BranchFinders) {
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

