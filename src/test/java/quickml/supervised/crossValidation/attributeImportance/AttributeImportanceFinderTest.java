package quickml.supervised.crossValidation.attributeImportance;


import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import quickml.data.InstanceWithAttributesMap;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.InstanceLoader;
import quickml.supervised.classifier.tree.TreeBuilder;
import quickml.supervised.classifier.tree.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;

import java.util.List;
import java.util.Set;

public class AttributeImportanceFinderTest {


    private List<InstanceWithAttributesMap> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }


    @Test
    public void testAttributeImportanceFinder() throws Exception {
        AttributeImportanceFinder<InstanceWithAttributesMap> attributeImportanceFinder = new AttributeImportanceFinderBuilder<>()
                .modelBuilder(new RandomForestBuilder(new TreeBuilder(new GiniImpurityScorer()).maxDepth(16).minCategoricalAttributeValueOccurances(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7))).numTrees(5))
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