package quickml.supervised.crossValidation.attributeImportance;


import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import quickml.InstanceLoader;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTreeBuilder;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers.GiniImpurityOldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;


import java.util.List;
import java.util.Set;

public class AttributeImportanceFinderIntegrationTestOld {


    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }


    @Test
    public void testAttributeImportanceFinder() throws Exception {
        System.out.println("\n \n \n new  attrImportanceTest\n\n\n");
        OldTreeBuilder modelBuilder = new OldTreeBuilder().scorer(new GiniImpurityOldScorer()).maxDepth(16).minCategoricalAttributeValueOccurances(11).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));

        AttributeImportanceFinder<ClassifierInstance> attributeImportanceFinder = new AttributeImportanceFinderBuilder<ClassifierInstance>()
                .modelBuilder(modelBuilder)
                .dataCycler(new OutOfTimeData<ClassifierInstance>(instances, .25, 12, new OnespotDateTimeExtractor()))
                .percentAttributesToRemovePerIteration(0.3)
                .numOfIterations(3)
                .attributesToKeep(attributesToKeep())
                .primaryLossFunction(new WeightedAUCCrossValLossFunction(1.0))//NonWeightedAUCCrossValLossFunction())//AUCCrossValLossFunction(1.0))//new ClassifierLogCVLossFunction(.000001))
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