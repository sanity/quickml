package quickml.supervised.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.Utils;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducer;
import quickml.supervised.tree.summaryStatistics.TrainingDataReducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.initializers.Initializer;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.terminationConditions.BranchingConditions;
import quickml.supervised.tree.configurations.TreeBuildContext;
import quickml.supervised.tree.configurations.TreeConfig;

import java.util.*;

public class TreeBuilderHelper<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>>  {

    //has build parameters: branch builders, scorer, leaf builder: stateless w.r.t to training data, and not shared with any other class.
    protected TreeConfig<L, I, VC, N> treeConfig;
    private Initializer initializer;

    public TreeBuilderHelper(TreeConfig<L, I, VC, N> treeConfig, Initializer initializer) {
        this.treeConfig = treeConfig.copy();
    }

    public TreeBuilderHelper<L, I, VC, N> copy() {
        return new TreeBuilderHelper(treeConfig, initializer);
    }

    public void updateBuilderConfig(Map<String, Object> cfg) {
        treeConfig.update(cfg);
    }

    public N computeNodesAndLinks(List<I> trainingData) {
        TreeBuildContext<L, I, VC, N> tbc = initializer.initialize(treeConfig, trainingData);
        return createNode(null, trainingData, tbc);
    }

    private N createNode(Branch<VC, N> parent, List<I> trainingData, TreeBuildContext<L, I, VC, N> tbc) {
        Preconditions.checkArgument(trainingData == null || trainingData.isEmpty(), "Can't build a tree with no training data");
        BranchingConditions<VC, N> branchingConditions = tbc.getBranchingConditions();
        VC aggregateStats = getAggregateStats(tbc, parent, trainingData);
        if (branchingConditions.canTryAddingChildren(parent, aggregateStats)) {
            return (N)getLeaf(parent, aggregateStats, tbc); //cast 100% guaranteed, as Leaf<VC,N> extends N
        }
        Optional<? extends Branch<VC, N>> bestBranchOptional = findBestBranch(parent, trainingData, tbc);
        if (!bestBranchOptional.isPresent()) {
            return (N)getLeaf(parent, aggregateStats, tbc);//cast 100% guaranteed, as Leaf<VC,N> extends N
        }
        Branch<VC, N> bestBranch = bestBranchOptional.get();
        Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        bestBranch.trueChild = createNode(bestBranch, trueFalsePair.trueTrainingSet, tbc);
        bestBranch.falseChild = createNode(bestBranch, trueFalsePair.falseTrainingSet, tbc);

        return (N)bestBranch;//cast 100% guaranteed, as Branch<VC,N> extends N.
    }

    private Optional<? extends Branch<VC, N>> findBestBranch(Branch parent, List<I> instances, TreeBuildContext<L, I, VC, N> tbc ) {
        double bestScore = 0;
        Optional<? extends Branch<VC, N>> bestBranchOptional = Optional.absent();
        List<BranchFinderAndReducer<L, I, VC, N>> branchFindersAndReducers = tbc.getBranchFindersAndReducers();
        for (BranchFinderAndReducer<L, I, VC, N> branchFinderAndReducer : branchFindersAndReducers) {
            //important to keep the reduction of instances to ValueCounters separate from branchFinders, which don't need to know anything about the form of the instances
            TrainingDataReducer<L, I, VC> trainingDataReducer = branchFinderAndReducer.getReducer();
            trainingDataReducer.setTrainingData(instances);
            BranchFinder<VC, N> branchFinder = branchFinderAndReducer.getBranchFinder();
            Optional<? extends Branch<VC, N>> thisBranchOptional = branchFinder.findBestBranch(parent, trainingDataReducer); //decoupling occurs bc trainingDataReducer implements a simpler interface than TraingDataReducer
            if (thisBranchOptional.isPresent()) {
                Branch<VC, N> thisBranch = thisBranchOptional.get();
                if (isBestSplitSoFar(tbc, bestScore, thisBranch)) {  //minScore evaluation delegated to branchFinder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private boolean isBestSplitSoFar(TreeBuildContext<L, I, VC, N> tbc, double bestScore, Branch<VC, N> thisBranch) {
        return thisBranch.getScore()> bestScore && !tbc.getBranchingConditions().isInvalidSplit(thisBranch.getScore());
    }

    protected  Leaf<VC, N> getLeaf(Branch<VC, N> parent, VC valueCounter, TreeBuildContext<L, I, VC, N> tbc) {
            return tbc.getLeafBuilder().buildLeaf(parent, valueCounter);
    }

    private VC getAggregateStats(TreeBuildContext<L, I, VC, N> tbc, Branch<VC, N> parent, List<I> trainingData) {
        return !parent.isEmpty() ? parent.valueCounter : tbc.getValueCounterProducer().getValueCounter(trainingData);
    }
}

