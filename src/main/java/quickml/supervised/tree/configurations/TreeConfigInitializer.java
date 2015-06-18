package quickml.supervised.tree.configurations;

import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.BranchFinderBuilder;
import quickml.supervised.tree.constants.BranchType;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/22/15.
 */
//how strongly is this coupled to the TreeConfig


public abstract  class TreeConfigInitializer<L, I extends InstanceWithAttributesMap<L>, TS extends ValueCounter<TS>> {

    public StateAssociatedWithATreeBuild<TS, D> createTreeConfig(Bagging.TrainingDataPair<L, I> trainingDataPair, TreeConfig<TS, D> fcb) {
        D dataProperties = getDataProperties(trainingDataPair.trainingData, fcb.getBranchTypes());
        List<BranchFinder<TS>> initializedBranchFinders = initializeBranchFinders(fcb, dataProperties);
        return new StateAssociatedWithATreeBuild<TS, D>(fcb.getBranchingConditions(), fcb.getScorer(), initializedBranchFinders,
                fcb.buildLeaf(), fcb.getBagging(), dataProperties, trainingDataPair.outOfBagTrainingData);
    }

    private List<BranchFinder<TS>> initializeBranchFinders(TreeConfig<TS, D> fcb, D dataProperties) {
        List<BranchFinder<TS>> initializedBranchFinders = Lists.newArrayList();
        for (BranchFinderBuilder<TS, D> branchFinderBuilder : fcb.getBranchFinderBuilders()) {
            branchFinderBuilder.setScorer(fcb.getScorer());
            branchFinderBuilder.setBranchingConditions(fcb.getBranchingConditions());
            initializedBranchFinders.add(branchFinderBuilder.buildBranchFinder(dataProperties));
        }
        return initializedBranchFinders;

    }

    protected abstract D getDataProperties(List<I> instances, Set<BranchType> branchTypes);

    public abstract TreeConfigInitializer<L, I, TS, D> copy();
}

