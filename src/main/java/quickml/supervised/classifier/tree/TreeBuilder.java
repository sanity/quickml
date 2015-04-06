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

public final class TreeBuilder<T extends InstanceWithAttributesMap, S extends SplitProperties, D extends DataProperties> implements PredictiveModelBuilder<Tree, T> {
//TO
    private ForestConfigBuilder<T, S, D> configBuilder;
    private ForestConfig<T, S, D> forestConfig;
    private Optional<List<T>> outOfBagTrainingData = Optional.absent();


    public TreeBuilder(ForestConfigBuilder config) {
        this.configBuilder = config;
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
        forestConfig = configBuilder.buildForestConfig(trainingData);
        return buildTree(trainingData);

    }

    //forest config should have a bagging object, since there can be more than one type of bagging
    private List<T> prepareTrainingData(Iterable<T> unprocessedTrainingData) {
        List<T> trainingData = Utils.<T>iterableToList(unprocessedTrainingData);
        if (forestConfig.getBagging().isPresent()) {
            Bagging<T> bagging = forestConfig.getBagging().get();
            Bagging.BaggedPair<T> baggedPair = bagging.separateTrainingDataFromOutOfBagData(trainingData);
            this.outOfBagTrainingData = Optional.of(baggedPair.outOfBagTrainingData);
            trainingData = baggedPair.baggedTrainingData;
        }
        return trainingData;
    }

    private Tree buildTree(List<T> trainingData) {
        Branch parent = null; //parent of root should be null
        Node root = createNode(parent, trainingData);
        TreeFactory treeFactory = forestConfig.getTreeFactory();
        Tree tree = treeFactory.constructTree(root, forestConfig.getDataProperities());
        if (canPrune()) {
            tree = forestConfig.getPostPruningStrategy().get().prune(tree, outOfBagTrainingData.get());
        }
        return tree;
    }

    private boolean canPrune() {
        return forestConfig.getPostPruningStrategy().isPresent() && outOfBagTrainingData.isPresent();
    }

    private Node createNode(Branch parent, List<T> trainingData) {
        Preconditions.checkArgument(trainingData == null || trainingData.size() ==0, "Can't build a tree with no training data");
        TerminationConditions<T, S> terminationConditions = forestConfig.getTerminationConditions();
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
        return forestConfig.getLeafBuilder().buildLeaf(parent, trainingData);
    }

    private Optional<? extends Branch> findBestBranch(Branch parent, List<T> instances) {

        double bestScore = forestConfig.getTerminationConditions().getMinScore();
        Optional<? extends Branch> bestBranchOptional = Optional.absent();
        Iterable<BranchFinder<T>> BranchFinders = forestConfig.getBranchFinders();
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

