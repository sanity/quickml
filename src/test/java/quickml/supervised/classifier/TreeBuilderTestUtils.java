package quickml.supervised.classifier;

import org.joda.time.DateTime;
import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static quickml.collections.MapUtils.random;

/**
 * Created by Chris on 5/14/2014.
 */
public class TreeBuilderTestUtils {

    public static List<InstanceWithAttributesMap> getInstances(int numInstances) {
        final List<InstanceWithAttributesMap> instances = new ArrayList<>();
        for (int x = 0; x < numInstances; x++) {
            final double height = (4 * 12) + random.nextInt(3 * 12);
            final double weight = 120 + random.nextInt(110);
            AttributesMap attributes = AttributesMap.newHashMap();
            attributes.put("weight", weight);
            attributes.put("height", height);
            attributes.put("gender", random.nextInt(2));
            attributes.put("other", random.nextInt(2));
            instances.add(new InstanceWithAttributesMap(attributes, bmiHealthy(weight, height)));
        }
        return instances;
    }

    public static List<InstanceWithAttributesMap> getIntegerInstances(int numInstances) {
        final List<InstanceWithAttributesMap> instances = new ArrayList<>();
        for (int x = 0; x < numInstances; x++) {
            final double height = (4 * 12) + random.nextInt(3 * 12);
            final double weight = 120 + random.nextInt(110);
            final AttributesMap attributes = AttributesMap.newHashMap();
            DateTime date = new DateTime();
            addDateAttributes(attributes, date.getYear(), date.getMonthOfYear(), (random.nextInt(28) + 1), random.nextInt(24));
            attributes.put("weight", weight);
            attributes.put("height", height);
            instances.add(new InstanceWithAttributesMap(attributes, bmiHealthyInteger(weight, height)));
        }
        return instances;
    }

    public static List<InstanceWithAttributesMap> getInstancesOneEveryHour(int numInstances) {
        DateTime date = new DateTime().minusHours(numInstances + 10);
        final List<InstanceWithAttributesMap> instances = new ArrayList<>();
        for (int x = 0; x < numInstances; x++) {
            final double height = (4 * 12) + random.nextInt(3 * 12);
            final double weight = 120 + random.nextInt(110);
            final AttributesMap attributes = AttributesMap.newHashMap();

            addDateAttributes(attributes, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), date.getHourOfDay());
            date = date.plusHours(1);

            attributes.put("weight", weight);
            attributes.put("height", height);
            instances.add(new InstanceWithAttributesMap(attributes, bmiHealthyInteger(weight, height)));
        }
        return instances;
    }

    private static void addDateAttributes(AttributesMap attributes, int year, int month, int day, int hour) {

        attributes.put("timeOfArrival-year", (double) year);
        attributes.put("timeOfArrival-monthOfYear", (double) month);
        attributes.put("timeOfArrival-dayOfMonth", (double) day);
        attributes.put("timeOfArrival-hourOfDay", (double) hour);
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
