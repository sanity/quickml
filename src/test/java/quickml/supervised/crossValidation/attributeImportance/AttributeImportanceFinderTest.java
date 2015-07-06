package quickml.supervised.crossValidation.attributeImportance;


import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.InstanceLoader;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.tree.nodes.LeafDepthStats;

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
        System.out.println("\n \n \n new  attrImportanceTest");
        DecisionTreeBuilder modelBuilder = new DecisionTreeBuilder().scorer(new GiniImpurityScorer()).maxDepth(16).minLeafInstances(0).minAttributeValueOccurences(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));
        DecisionTree decisionTree = modelBuilder.buildPredictiveModel(instances);
        RandomDecisionForestBuilder randomDecisionForestBuilder = new RandomDecisionForestBuilder(modelBuilder).numTrees(20);
        LeafDepthStats stats = new LeafDepthStats();
        decisionTree.root.calcMeanDepth(stats);
        double meanDepth = (1.0*stats.ttlDepth)/stats.ttlSamples;
        System.out.println("weighted depth " + stats.ttlDepth + " numSamples: " + stats.ttlSamples + "mean depth " + meanDepth);

        CrossValidator<AttributesMap, Classifier, ClassifierInstance> cv = new CrossValidator<>(modelBuilder,
                new ClassifierLossChecker<ClassifierInstance>(new ClassifierLogCVLossFunction(.000001)),
                new OutOfTimeData<>(instances, .25, 12, new OnespotDateTimeExtractor() ) );
        for (int i =0; i<20; i++) {
            System.out.println("Loss: " + cv.getLossForModel());
        }
/*
        AttributeImportanceFinder<ClassifierInstance> attributeImportanceFinder = new AttributeImportanceFinderBuilder<>()
                .modelBuilder(modelBuilder)
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