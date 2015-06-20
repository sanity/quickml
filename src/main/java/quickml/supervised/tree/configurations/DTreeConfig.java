package quickml.supervised.tree.configurations;

import com.google.common.collect.Lists;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.completeDataSetSummaries.DTreeTrainingDataSurveyor;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.DTNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class DTreeConfig<I extends ClassifierInstance> extends TreeConfig<Object, I, ClassificationCounter, DTNode> {

    @Override
    public DTreeConfig<I> createTreeConfig() {
        return new DTreeConfig<>();
    }

    @Override
    //this method has knowledge of the internals of the treeConfig...meaning it should be an abstract method in the TreeConfig...not some other class

    public TreeBuildContext<Object, I, ClassificationCounter, DTNode> initialize(List<I> trainingData) {
        boolean considerBooleanAttributes = hasBranchFinderBuilder(BranchType.BOOLEAN);
        DTreeTrainingDataSurveyor<I> DTTrainingDataSurveyor = new DTreeTrainingDataSurveyor<>(considerBooleanAttributes);
        Map<BranchType, Set<String>> candidateAttributesByBranchType = DTTrainingDataSurveyor.groupAttributesByType(trainingData);
        List<BranchFinder<ClassificationCounter, DTNode>> branchFinders = intializeBranchFinders(trainingData, candidateAttributesByBranchType);
        //valueCounterAndReducer?
        return new TreeBuildContext<>(branchingConditions, scorer, ,leafBuilder);

    }

    private List<BranchFinder<ClassificationCounter, DTNode>> intializeBranchFinders(List<I> trainingData, Map<BranchType, Set<String>> candidateAttributesByBranchType) {
        List<BranchFinder<ClassificationCounter, DTNode>> branchFinders = Lists.newArrayList();
        ClassificationCounter classificationCounts = valueCounterProducer.getValueCounter(trainingData);
        int numClasses = classificationCounts.allClassifications().size();

        for (BranchFinderBuilder<ClassificationCounter, DTNode> branchFinderBuilder : getBranchFinderBuilders()) {
            if (useBranchFinder(branchFinderBuilder, numClasses)) {
                BranchFinder<ClassificationCounter, DTNode> branchFinder = branchFinderBuilder.buildBranchFinder(classificationCounts, candidateAttributesByBranchType.get(BranchType.BINARY_CATEGORICAL));
                branchFinders.add(branchFinder);
            }
        }
        return branchFinders;
    }

    private boolean useBranchFinder(BranchFinderBuilder<ClassificationCounter, DTNode> branchFinderBuilder, int numClasses) {
           if (branchFinderBuilder.getBranchType().equals(BranchType.BINARY_CATEGORICAL) && numClasses != 2)  {
               return false;
           }

           if (branchFinderBuilder.getBranchType().equals(BranchType.CATEGORICAL) && numClasses== 2)  {
               return false;
           }
           return true;
       }
}
