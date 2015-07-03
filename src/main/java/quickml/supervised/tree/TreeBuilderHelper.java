package quickml.supervised.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.ClassifierInstance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.Utils;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducer;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.branchFinders.DTBinaryCatBranchFinder;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.treeBuildContexts.TreeContext;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;

import java.io.Serializable;
import java.util.*;

public class TreeBuilderHelper<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>>  {

    protected TreeContextBuilder<I, VC> treeContextBuilder;

    public TreeBuilderHelper(TreeContextBuilder<I, VC> treeContextBuilder) {
        this.treeContextBuilder = treeContextBuilder.copy();
    }

    public TreeBuilderHelper<I, VC> copy() {
        return new TreeBuilderHelper(treeContextBuilder);
    }

    public void updateBuilderConfig(Map<String, Serializable> cfg) {
        treeContextBuilder.updateBuilderConfig(cfg);
    }

    public Node<VC> computeNodes(List<I> trainingData) {
        TreeContext<I, VC> itbc = treeContextBuilder.buildContext(trainingData);
        return createNode(null, trainingData, itbc);
    }

    protected Node<VC> createNode(Branch<VC> parent, List<I> trainingData, TreeContext<I, VC> tc) {
        Preconditions.checkArgument(trainingData != null && !trainingData.isEmpty(), "Can't build a tree with no training data");
        BranchingConditions<VC> branchingConditions = tc.getBranchingConditions();
        VC aggregateStats = getAggregateStats(tc,trainingData);
        if (!branchingConditions.canTryAddingChildren(parent, aggregateStats)) {
            return getLeaf(parent, aggregateStats, tc);
        }
        Optional<? extends Branch<VC>> bestBranchOptional = findBestBranch(parent, trainingData, tc);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, aggregateStats, tc);
        }
        Branch<VC> bestBranch = bestBranchOptional.get();

        List<I> trueTrainingSet = Lists.newArrayList();
        List<I> falseTrainingSet = Lists.newArrayList();
        setTrueAndFalseTrainingSets(trainingData, bestBranch, trueTrainingSet, falseTrainingSet);
        //Utils.TrueFalsePair<I> trueFalsePair = Utils.setTrueAndFalseTrainingSets(trainingData, bestBranch);

        if (trueTrainingSet.size() ==0 || falseTrainingSet.size() ==0){//trueFalsePair.falseTrainingSet.size() ==0 || trueFalsePair.trueTrainingSet.size() ==0) {
            return getLeaf(parent, aggregateStats, tc);
        }
        bestBranch.setTrueChild(createNode(bestBranch, trueTrainingSet, tc));
        bestBranch.setFalseChild(createNode(bestBranch, falseTrainingSet, tc));

        return bestBranch;
    }

    private Optional<? extends Branch<VC>> findBestBranch(Branch parent, List<I> instances, TreeContext<I, VC> tc ) {
        double bestScore = 0;
        Optional<? extends Branch<VC>> bestBranchOptional = Optional.absent();
        List<? extends BranchFinderAndReducer<I, VC>> branchFindersAndReducers = tc.getBranchFindersAndReducers();
        for (BranchFinderAndReducer<I, VC> branchFinderAndReducer : branchFindersAndReducers) {
            //important to keep the reduction of instances to ValueCounters separate from branchFinders, which don't need to know anything about the form of the instances
            Reducer<I, VC> reducer = branchFinderAndReducer.getReducer();
            reducer.setTrainingData(instances);
            BranchFinder<VC> branchFinder = branchFinderAndReducer.getBranchFinder();
            if (branchFinder.getBranchType().equals(BranchType.NUMERIC)) {
                continue;
            }
            Optional<? extends Branch<VC>> thisBranchOptional = branchFinder.findBestBranch(parent, reducer); //decoupling occurs bc trainingDataReducer implements a simpler interface than TraingDataReducer
            if (thisBranchOptional.isPresent()) {
                Branch<VC> thisBranch = thisBranchOptional.get();
                if (isBestSplitSoFar(tc, bestScore, thisBranch)) {  
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private boolean isBestSplitSoFar(TreeContext<I, VC> itbc, double bestScore, Branch<VC> thisBranch) {
        return thisBranch.getScore()> bestScore && !itbc.getBranchingConditions().isInvalidSplit(thisBranch.getScore());
    }

    protected  Leaf<VC> getLeaf(Branch<VC> parent, VC valueCounter, TreeContext<I, VC> itbc) {
        Leaf<VC> vcnLeaf = itbc.getLeafBuilder().buildLeaf(parent, valueCounter);
        return vcnLeaf;
    }

    private VC getAggregateStats(TreeContext<I, VC> itbc,List<I> trainingData) {
        return itbc.getValueCounterProducer().getValueCounter(trainingData);
    }
    private void setTrueAndFalseTrainingSets(Iterable<I> trainingData, Branch<VC> bestNode, List<I> trueTrainingSet, List<I> falseTrainingSet) {
        //put instances with attribute values into appropriate training sets
        for (I instance : trainingData) {
            if (bestNode.decide(instance.getAttributes())) {
                trueTrainingSet.add(instance);
            } else {
                falseTrainingSet.add(instance);
            }
        }
    }
}

