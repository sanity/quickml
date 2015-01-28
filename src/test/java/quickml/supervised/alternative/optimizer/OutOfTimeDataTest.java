package quickml.supervised.alternative.optimizer;

import org.junit.Before;
import org.junit.Test;
import quickml.data.AttributesMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class OutOfTimeDataTest {

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void testOutOfTimeData() throws Exception {
        ArrayList<ClassifierInstance> instances = new ArrayList<>();

        // Create an instance for 6 consecutive days
        instances.add(createInstance(1));
        instances.add(createInstance(2));
        instances.add(createInstance(3));
        instances.add(createInstance(4));
        instances.add(createInstance(5));
        instances.add(createInstance(6));

        OutOfTimeData<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(instances, 0.5, 24);
        List<ClassifierInstance> trainingSet = outOfTimeData.getTrainingSet();


        // Verifiy the inital data is split up - half for the training set, and one entry in the validation set
        assertEquals(3, trainingSet.size());
        assertDayOfMonthMatches(trainingSet.get(0), 1);
        assertDayOfMonthMatches(trainingSet.get(1), 2);
        assertDayOfMonthMatches(trainingSet.get(2), 3);

        List<ClassifierInstance> validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 4);

        // Verify that we have increased the training set size and moved on to the next validation set
        outOfTimeData.nextValidationSet();
        assertEquals(4, outOfTimeData.getTrainingSet().size());
        validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 5);

        // Verify that we have increased the training set size and moved on to the next validation set
        outOfTimeData.nextValidationSet();
        assertEquals(5, outOfTimeData.getTrainingSet().size());
        validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 6);

        // Verify that there are no more iterations
        outOfTimeData.nextValidationSet();
        assertFalse(outOfTimeData.hasMore());
    }

    @Test
    public void testValidateIfThereIsABreakInTheDataWeMoveOnToTheNextPeriod() throws Exception {

        List<ClassifierInstance> instances = new ArrayList<>();

        // Create an instance for 3 days, with a gap after the first two
        instances.add(createInstance(1));
        instances.add(createInstance(2));
        instances.add(createInstance(4));

        OutOfTimeData<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(instances, 0.5, 24);


        // Verifiy the inital data is split up - half for the training set, and one entry in the validation set
        List<ClassifierInstance> trainingSet = outOfTimeData.getTrainingSet();
        List<ClassifierInstance> validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, trainingSet.size());
        assertDayOfMonthMatches(trainingSet.get(0), 1);
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 2);

        // Verify that we skipped past the missing day and the validation set includes the next day
        outOfTimeData.nextValidationSet();
        assertEquals(2, outOfTimeData.getTrainingSet().size());
        assertDayOfMonthMatches(outOfTimeData.getValidationSet().get(0), 4);

    }

    private void assertDayOfMonthMatches(final ClassifierInstance instance, final int expected) {
        assertEquals(expected, instance.getTimestamp().dayOfMonth().get());
    }

    private ClassifierInstance createInstance(final int day) {
        return new ClassifierInstance(createAttributes(day), 1.0D, 0.5);
    }

    private AttributesMap createAttributes(final double day) {
        AttributesMap attrs = AttributesMap.newHashMap();
        attrs.put("timeOfArrival-year", 2015d);
        attrs.put("timeOfArrival-monthOfYear", 1d);
        attrs.put("timeOfArrival-dayOfMonth", day);
        attrs.put("timeOfArrival-hourOfDay", 1d);
        attrs.put("timeOfArrival-minuteOfHour", 1d);
        return attrs;
    }


}