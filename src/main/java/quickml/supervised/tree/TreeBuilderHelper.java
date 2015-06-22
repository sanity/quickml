package quickml.supervised.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.Utils;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducer;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.treeBuildContexts.TreeContext;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;

import java.util.*;

public class TreeBuilderHelper<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>, N extends Node<VC, N>>  {

    protected TreeContextBuilder<I, VC, N> treeContextBuilder;

    public TreeBuilderHelper(TreeContextBuilder<I, VC, N> treeContextBuilder) {
        this.treeContextBuilder = treeContextBuilder.copy();
    }

    public TreeBuilderHelper<I, VC, N> copy() {
        return new TreeBuilderHelper(treeContextBuilder);
    }

    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeContextBuilder.update(cfg);
    }

    public N computeNodes(List<I> trainingData) {
        TreeContext<I, VC, N> itbc = treeContextBuilder.buildContext(trainingData);
        return createNode(null, trainingData, itbc);
    }

    protected N createNode(Branch<VC, N> parent, List<I> trainingData, TreeContext<I, VC, N> itbc) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        BranchingConditions<VC, N> branchingConditions = itbc.getBranchingConditions();
        VC aggregateStats = getAggregateStats(itbc, parent, trainingData);
        if (branchingConditions.canTryAddingChildren(parent, aggregateStats)) {
            return (N)getLeaf(parent, aggregateStats, itbc); //cast 100% guaranteed, as Leaf<VC,N> extends N
        }
        Optional<? extends Branch<VC, N>> bestBranchOptional = findBestBranch(parent, trainingData, itbc);
        if (!bestBranchOptional.isPresent()) {
            return (N)getLeaf(parent, aggregateStats, itbc);//cast 100% guaranteed, as Leaf<VC,N> extends N
        }
        Branch<VC, N> bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet, itbc);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet, itbc);

        return (N)bestBranch;//cast 100% guaranteed, as Branch<VC,N> extends N.
    }

    private Optional<? extends Branch<VC, N>> findBestBranch(Branch parent, List<I> instances, TreeContext<I, VC, N> itbc ) {
        double bestScore = 0;
        Optional<? extends Branch<VC, N>> bestBranchOptional = Optional.absent();
        List<? extends BranchFinderAndReducer<I, VC, N>> branchFindersAndReducers = itbc.getBranchFindersAndReducers();
        for (BranchFinderAndReducer<I, VC, N> branchFinderAndReducer : branchFindersAndReducers) {
            //important to keep the reduction of instances to ValueCounters separate from branchFinders, which don't need to know anything about the form of the instances
            Reducer<I, VC> reducer = branchFinderAndReducer.getReducer();
            reducer.setTrainingData(instances);
            BranchFinder<VC, N> branchFinder = branchFinderAndReducer.getBranchFinder();
            Optional<? extends Branch<VC, N>> thisBranchOptional = branchFinder.findBestBranch(parent, reducer); //decoupling occurs bc trainingDataReducer implements a simpler interface than TraingDataReducer
            if (thisBranchOptional.isPresent()) {
                Branch<VC, N> thisBranch = thisBranchOptional.get();
                if (isBestSplitSoFar(itbc, bestScore, thisBranch)) {  //minScore evaluation delegated to branchFinder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private boolean isBestSplitSoFar(TreeContext<I, VC, N> itbc, double bestScore, Branch<VC, N> thisBranch) {
        return thisBranch.getScore()> bestScore && !itbc.getBranchingConditions().isInvalidSplit(thisBranch.getScore());
    }

    protected  Leaf<VC, N> getLeaf(Branch<VC, N> parent, VC valueCounter, TreeContext<I, VC, N> itbc) {
            return itbc.getLeafBuilder().buildLeaf(parent, valueCounter);
    }

    private VC getAggregateStats(TreeContext<I, VC, N> itbc, Branch<VC, N> parent, List<I> trainingData) {
        return !parent.isEmpty() ? parent.valueCounter : itbc.getValueCounterProducer().getValueCounter(trainingData);
    }
}

