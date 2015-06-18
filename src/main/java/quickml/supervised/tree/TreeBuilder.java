package quickml.supervised.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.Utils;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchSplitStatistics.AggregateStatistics;
import quickml.supervised.tree.branchSplitStatistics.TrainingDataReducer;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.terminationConditions.BranchingConditions;
import quickml.supervised.tree.configurations.StateAssociatedWithATreeBuild;
import quickml.supervised.tree.configurations.TreeConfig;
import quickml.supervised.tree.configurations.TreeConfigInitializer;

import java.util.*;

public abstract class TreeBuilder<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>>  {

    protected TreeConfig<L, I, VC, N> treeConfig;
    protected TreeConfigInitializer<L, I, VC> treeConfigInitializer;
    protected AggregateStatistics<L, I, VC> aggregateStatistics;

    protected TreeBuilder(TreeConfig<L, I, VC, N>  treeConfig, TreeConfigInitializer<L, I, VC> treeConfigInitializer, AggregateStatistics<L, I, VC> aggregateStatistics) {
        this.treeConfig = treeConfig.copy();
        this.treeConfigInitializer = treeConfigInitializer; //abstract factory pattern (applied to all public (stateless wrt training data) fields)
        this.aggregateStatistics = aggregateStatistics;// abstract factory pattern
    }


    public N computeRoot(Iterable<I> unprocessedTrainingData) {
        Bagging.TrainingDataPair<L, I> trainingDataPair = prepareTrainingData(unprocessedTrainingData);
        StateAssociatedWithATreeBuild<L, I, VC, N> stateAssociatedWithATreeBuild = treeConfigInitializer.createTreeConfig(trainingDataPair, treeConfig);  //make a method a param
        return createNode(null, trainingDataPair.trainingData, stateAssociatedWithATreeBuild);
    }

    private Bagging.TrainingDataPair<L, I> prepareTrainingData(Iterable<I> unprocessedTrainingData) {
        List<I> trainingData = Lists.newArrayList(unprocessedTrainingData);
        Bagging.TrainingDataPair<L, I> trainingDataPair = new Bagging.TrainingDataPair<>(trainingData, null);
        if (treeConfig.getBagging().isPresent()) {
            Optional<? extends Bagging> bagging = treeConfig.getBagging();
            trainingDataPair = bagging.get().separateTrainingDataFromOutOfBagData(trainingData);
        }
        return trainingDataPair;
    }


    private N createNode(Branch<VC, N> parent, List<I> trainingData, StateAssociatedWithATreeBuild<L, I, VC, N> stb) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        BranchingConditions<VC, N> branchingConditions = stb.getBranchingConditions();
        VC aggregateStats = getValueCounter(parent, trainingData);
        if (branchingConditions.canTryAddingChildren(parent, aggregateStats)) {
            return (N)getLeaf(parent, aggregateStats, stb); //cast 100% guaranteed, as Leaf<VC,N> extends N
        }
        Optional<? extends Branch<VC, N>> bestBranchOptional = findBestBranch(parent, trainingData, stb);
        if (!bestBranchOptional.isPresent()) {
            return (N)getLeaf(parent, aggregateStats, stb);//cast 100% guaranteed, as Leaf<VC,N> extends N
        }
        Branch<VC, N> bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet, stb);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet, stb);

        return (N)bestBranch;//cast 100% guaranteed, as Branch<VC,N> extends N.
    }

    private Optional<? extends Branch<VC, N>> findBestBranch(Branch parent, List<I> instances, StateAssociatedWithATreeBuild<L, I, VC, N> stb ) {

        double bestScore = 0;
        Optional<? extends Branch<VC, N>> bestBranchOptional = Optional.absent();
        Iterable<BranchFinder<VC, N>> branchFinders = stb.getBranchFinders();
        for (BranchFinder<VC, N> branchFinder : branchFinders) {
            //important to keep the reduction of instances to ValueCounters separate from branchFinders, which don't need to know anything about the form of the instances
            TrainingDataReducer<L, I, VC> trainingDataReducer = stb.getTrainingDataReducers(branchFinder.getBranchType());
            trainingDataReducer.setTrainingData(instances);
            Optional<? extends Branch<VC, N>> thisBranchOptional = branchFinder.findBestBranch(parent, trainingDataReducer); //decoupling occurs bc trainingDataReducer implements a simpler interface
            if (thisBranchOptional.isPresent()) {
                Branch<VC, N> thisBranch = thisBranchOptional.get();
                if (isBestSplitSoFar(stb, bestScore, thisBranch)) {  //minScore evaluation delegated to branchFinder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private boolean isBestSplitSoFar(StateAssociatedWithATreeBuild itc, double bestScore, Branch<VC, N> thisBranch) {
        return thisBranch.getScore()> bestScore && !itc.getBranchingConditions().isInvalidSplit(thisBranch.getScore());
    }

    private VC getValueCounter(Branch<VC, N> parent, List<I> trainingData) {
        return !parent.isEmpty() ? parent.valueCounter : aggregateStatistics.getAggregateStats(trainingData);
    }

    protected  Leaf<VC, N> getLeaf(Branch<VC, N> parent, VC valueCounter, StateAssociatedWithATreeBuild<L, I, VC, N> stb) {
            return stb.getLeafBuilder().buildLeaf(parent, valueCounter);
    }

    public abstract TreeBuilder<L, I, VC, N> copy();// {returns new TreeBuilder(configurations);}


    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeConfig.update(cfg);
    }


}

