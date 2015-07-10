package quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies.BinaryClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies.MultiClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.DTNumBranchFinder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class DTNumBranchFinderBuilder extends DTBranchFinderBuilder {

    @Override
    public DTNumBranchFinderBuilder createBranchFinderBuilder() {
        return new DTNumBranchFinderBuilder();
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.NUMERIC;
    }

    @Override
    public DTNumBranchFinder buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy;
        if (getAttributeValueIgnoringStrategyBuilder() == null) {
            if (classificationCounts.allClassifications().size() > 2) {
                MultiClassAttributeValueIgnoringStrategyBuilder multiClassAttributeValueIgnoringStrategyBuilder = new MultiClassAttributeValueIgnoringStrategyBuilder(getMinAttributeOccurences());
                attributeValueIgnoringStrategy = multiClassAttributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
            }
            else if (classificationCounts.allClassifications().size()== 2) {
                BinaryClassAttributeValueIgnoringStrategyBuilder binaryClassAttributeValueIgnoringStrategyBuilder = new BinaryClassAttributeValueIgnoringStrategyBuilder(getMinAttributeOccurences());
                attributeValueIgnoringStrategy = binaryClassAttributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
            }
            else {
                throw new RuntimeException("building decision oldTree with less than 2 classes.  numClassesIs0 = " + classificationCounts);
            }
        } else {
            attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        }
        return new DTNumBranchFinder(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }
}
