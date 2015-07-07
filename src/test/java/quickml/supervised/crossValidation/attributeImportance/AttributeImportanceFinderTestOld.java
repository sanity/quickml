package quickml.supervised.crossValidation.attributeImportance;


import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import quickml.InstanceLoader;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTree;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTreeBuilder;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers.GiniImpurityOldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;


import java.util.List;
import java.util.Set;

public class AttributeImportanceFinderTestOld {


    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }


    @Test
    public void testAttributeImportanceFinder() throws Exception {
        System.out.println("\n \n \n new  attrImportanceTest\n\n\n");
        OldTreeBuilder modelBuilder = new OldTreeBuilder().scorer(new GiniImpurityOldScorer()).maxDepth(16).minCategoricalAttributeValueOccurances(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));
     /*   Tree decisionTree = modelBuilder.buildPredictiveModel(instances);
        System.out.println(decisionTree.node.medianDepth());
//        RandomForestB randomDecisionForestBuilder = new RandomForestBuilder(modelBuilder).numTrees(20);

        CrossValidator<AttributesMap, Classifier, ClassifierInstance> cv = new CrossValidator<>(modelBuilder,
                new ClassifierLossChecker<ClassifierInstance>(new ClassifierLogCVLossFunction(.000001)),
                new OutOfTimeData<>(instances, .25, 12, new OnespotDateTimeExtractor() ) );
        for (int i =0; i<20; i++) {
            System.out.println("Loss: " + cv.getLossForModel());
        }
*/
        int depthZeros = 0;
        double meanDepthT = 0;
        double medianDepth = 0;
        for (int i = 0; i<50; i++) {
            OldTree oldTree = modelBuilder.buildPredictiveModel(instances);
            double meanDepthI = oldTree.oldNode.meanDepth();
            meanDepthT +=meanDepthI;
            medianDepth += oldTree.oldNode.medianDepth();
            if (meanDepthI < 1E-5) {
                depthZeros++;
            }
        }
        System.out.println("depth zeros " + depthZeros+ "mean depth: " + meanDepthT/50.0 + ". med depth: " + medianDepth/50.0);
        /*
        AttributeImportanceFinder<ClassifierInstance> attributeImportanceFinder = new AttributeImportanceFinderBuilder<>()
                .modelBuilder(new TreeBuilder().scorer(new GiniImpurityScorer()).maxDepth(16).minCategoricalAttributeValueOccurances(11).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7)))
                .dataCycler(new OutOfTimeData<>(instances, .25, 12, new OnespotDateTimeExtractor()))
                .percentAttributesToRemovePerIteration(0.3)
                .numOfIterations(3)
                .attributesToKeep(attributesToKeep())
                .primaryLossFunction(new ClassifierLogCVLossFunction(.000001))//ClassifierLogCVLossFunction(0.000001))
                .build();

        System.out.println(attributeImportanceFinder.determineAttributeImportance());
        */
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