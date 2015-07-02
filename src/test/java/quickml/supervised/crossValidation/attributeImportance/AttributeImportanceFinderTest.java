package quickml.supervised.crossValidation.attributeImportance;


import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.data.InstanceWithAttributesMap;
import quickml.data.OnespotDateTimeExtractor;
import quickml.InstanceLoader;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;

import java.util.List;
import java.util.Set;

public class AttributeImportanceFinderTest {


    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }


    @Test
    public void testAttributeImportanceFinder() throws Exception {
        AttributeImportanceFinder<ClassifierInstance> attributeImportanceFinder = new AttributeImportanceFinderBuilder<>()
                .modelBuilder(new RandomDecisionForestBuilder(new DecisionTreeBuilder().scorer(new GiniImpurityScorer()).maxDepth(16).minAttributeOccurences(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7))).numTrees(5))
                .dataCycler(new OutOfTimeData<>(instances, .25, 12, new OnespotDateTimeExtractor()))
                .percentAttributesToRemovePerIteration(0.3)
                .numOfIterations(3)
                .attributesToKeep(attributesToKeep())
                .primaryLossFunction(new ClassifierLogCVLossFunction(.000001))//ClassifierLogCVLossFunction(0.000001))
                .build();

        System.out.println(attributeImportanceFinder.determineAttributeImportance());
    }

    private Set<String> attributesToKeep() {
        Set<String> attributesToKeepRegardessOfQuality = Sets.newHashSet();
        attributesToKeepRegardessOfQuality.add("timeOfArrival-year");
        attributesToKeepRegardessOfQuality.add("timeOfArrival-monthOfYear");
        attributesToKeepRegardessOfQuality.add("timeOfArrival-dayOfMonth");
        attributesToKeepRegardessOfQuality.add("timeOfArrival-hourOfDay");
        attributesToKeepRegardessOfQuality.add("timeOfArrival-minuteOfHour");
        attributesToKeepRegardessOfQuality.add("internalCreativeId");
        attributesToKeepRegardessOfQuality.add("domain");
        return attributesToKeepRegardessOfQuality;
    }

}