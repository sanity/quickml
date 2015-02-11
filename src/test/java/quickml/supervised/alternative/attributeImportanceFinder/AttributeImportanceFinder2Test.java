package quickml.supervised.alternative.attributeImportanceFinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import quickml.supervised.InstanceLoader;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossFunction;
import quickml.supervised.alternative.crossValidationLoss.ClassifierRMSELossFunction;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.alternative.optimizer.OnespotDateTimeExtractor;
import quickml.supervised.alternative.optimizer.OutOfTimeData;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.WeightedAUCCrossValLossFunction;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.charset.Charset.defaultCharset;

public class AttributeImportanceFinder2Test {


    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }


    @Test
    public void testAttributeImportanceFinder() throws Exception {
        OutOfTimeData<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(instances, .2, 5, new OnespotDateTimeExtractor());

        List<ClassifierLossFunction> lossFunctions = Lists.newArrayList();
        lossFunctions.add(new WeightedAUCCrossValLossFunction(1.0));

        RandomForestBuilder randomForestBuilder = new RandomForestBuilder().numTrees(5);
        AttributeImportanceFinder2 importanceFinder = new AttributeImportanceFinder2(randomForestBuilder, outOfTimeData, .2, 3, attributesToKeep(), lossFunctions, "RMSE");
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