package quickml.supervised.alternative.attributeImportanceFinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import quickml.supervised.InstanceLoader;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLogCVLossFunction;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossFunction;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.alternative.optimizer.OnespotDateTimeExtractor;
import quickml.supervised.alternative.optimizer.OutOfTimeData;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.util.List;
import java.util.Set;

public class AttributeImportanceFinder2Test {


    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }


    @Ignore("WIP")
    @Test
    public void testAttributeImportanceFinder() throws Exception {
        OutOfTimeData<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(instances, .2, 5, new OnespotDateTimeExtractor());

        List<ClassifierLossFunction> lossFunctions = Lists.newArrayList();
        lossFunctions.add(new ClassifierLogCVLossFunction(0.000001));

        RandomForestBuilder randomForestBuilder = new RandomForestBuilder().numTrees(5);
        AttributeImportanceFinder2 importanceFinder = new AttributeImportanceFinder2(randomForestBuilder, outOfTimeData, .2, 10, attributesToKeep(), lossFunctions, ClassifierLogCVLossFunction.NAME);
        List<AttributeLossTracker> attributeLossTrackers = importanceFinder.determineAttributeImportance();

        System.out.println(attributeLossTrackers);
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