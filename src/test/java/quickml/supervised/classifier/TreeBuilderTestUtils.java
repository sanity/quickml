package quickml.supervised.classifier;

import org.joda.time.DateTime;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.supervised.alternative.optimizer.ClassifierInstance;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Chris on 5/14/2014.
 */
public class TreeBuilderTestUtils {

    public static List<ClassifierInstance> getInstances(int numInstances) {
        final List<ClassifierInstance> instances = new ArrayList<>();
        for (int x = 0; x < numInstances; x++) {
            final double height = (4 * 12) + MapUtils.random.nextInt(3 * 12);
            final double weight = 120 + MapUtils.random.nextInt(110);
            AttributesMap attributes = AttributesMap.newHashMap();
            attributes.put("weight", weight);
            attributes.put("height", height);
            attributes.put("gender", MapUtils.random.nextInt(2));
            instances.add(new ClassifierInstance(attributes, bmiHealthy(weight, height)));
        }
        return instances;
    }

    public static List<ClassifierInstance> getIntegerInstances(int numInstances) {
        final List<ClassifierInstance> instances = new ArrayList<>();
        for (int x = 0; x < numInstances; x++) {
            final double height = (4 * 12) + MapUtils.random.nextInt(3 * 12);
            final double weight = 120 + MapUtils.random.nextInt(110);
            DateTime dateTime = new DateTime();
            final double year = dateTime.getYear();
            final double month = dateTime.getMonthOfYear();
            final double day = MapUtils.random.nextInt(28) + 1;
            final double hour = MapUtils.random.nextInt(24);
            final AttributesMap attributes = AttributesMap.newHashMap();
            attributes.put("weight", weight);
            attributes.put("height", height);
            attributes.put("timeOfArrival-year", year);
            attributes.put("timeOfArrival-monthOfYear", month);
            attributes.put("timeOfArrival-dayOfMonth", day);
            attributes.put("timeOfArrival-hourOfDay", hour);
            instances.add(new ClassifierInstance(attributes, bmiHealthyInteger(weight, height)));
        }
        return instances;
    }

    public static void serializeDeserialize(final Serializable object) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object deserialized = objectInputStream.readObject();
        objectInputStream.close();
    }

    public static String bmiHealthy(final double weightInPounds, final double heightInInches) {
        final double bmi = bmi(weightInPounds, heightInInches);
        if (bmi < 20)
            return "underweight";
        else if (bmi > 25)
            return "overweight";
        else
            return "healthy";
    }

    public static Serializable bmiHealthyInteger(final double weightInPounds, final double heightInInches) {
        final double bmi = bmi(weightInPounds, heightInInches);
        if (bmi > 25)
            return 0;
        else
            return 1;
    }

    public static double bmi(final double weightInPounds, final double heightInInches) {
        return (weightInPounds / (heightInInches * heightInInches)) * 703;
    }
}
