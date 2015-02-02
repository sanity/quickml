package quickml.supervised.alternative.attributeImportanceFinder;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossFunction;
import quickml.supervised.alternative.crossValidationLoss.ClassifierRMSELossFunction;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.alternative.optimizer.OutOfTimeData;
import quickml.supervised.classifier.decisionTree.TreeBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.Charset.defaultCharset;

public class AttribributeImportanceFinder2Test {


    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = loadOnespotData();
    }


    @Ignore("WIP")
    @Test
    public void testAttributeImportanceFinder() throws Exception {
        OutOfTimeData<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(instances, .2, 5);

        Map<String, ClassifierLossFunction> lossFunctionMap = Maps.newHashMap();
        lossFunctionMap.put("RMSE", new ClassifierRMSELossFunction());

        AttribributeImportanceFinder2 importanceFinder = new AttribributeImportanceFinder2(new TreeBuilder(), outOfTimeData, .2, 10, attributesToKeep(), lossFunctionMap, "RMSE");
        List<AttributeLossTracker> attributeLossTrackers = importanceFinder.determineAttributeImportance();

        System.out.println(attributeLossTrackers);
    }

    private List<ClassifierInstance> loadOnespotData() throws IOException {
        URL resource = Resources.getResource("onespot_training_instances_large.json");
        CharSource charSource = Resources.asCharSource(resource, defaultCharset());

        Gson gson = createGson();

        JsonReader reader = new JsonReader(charSource.openStream());
        List<ClassifierInstance> instances = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            ClassifierInstance instance = gson.fromJson(reader, ClassifierInstance.class);
            instances.add(instance);
        }
        reader.endArray();
        reader.close();
        return instances;

    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Serializable.class, new SerializableDeserializer());
        return gsonBuilder.create();
    }


    private class SerializableDeserializer implements JsonDeserializer<Serializable> {
        public Serializable deserialize(JsonElement json, Type Serializable, JsonDeserializationContext context) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean())
                return primitive.getAsBoolean();
            else if (primitive.isNumber()) {
                double value = primitive.getAsDouble();
                if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
                    return (int) value;
                } else {
                    return value;
                }
            } else if (primitive.isString())
                return primitive.getAsString();
            throw new RuntimeException("Unexpected type when parsing Instance json " + json.toString());
        }
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