package quickml.supervised.alternative.optimizer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static quickml.TestUtils.createClassifierInstance;

public class OutOfTimeDataTest {

    private OutOfTimeData<ClassifierInstance> outOfTimeData;
    private OnespotDateTimeExtractor dateTimeExtractor;

    @Before
    public void setUp() throws Exception {
        dateTimeExtractor = new OnespotDateTimeExtractor();
        List<ClassifierInstance> instances = new ArrayList<>();

        // Create an instance for 6 consecutive days
        instances.add(createClassifierInstance(1));
        instances.add(createClassifierInstance(2));
        instances.add(createClassifierInstance(3));
        instances.add(createClassifierInstance(4));
        instances.add(createClassifierInstance(5));
        instances.add(createClassifierInstance(6));

        outOfTimeData = new OutOfTimeData<>(instances, 0.5, 24, dateTimeExtractor);
    }


    @Test
    public void testOutOfTimeData() throws Exception {

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
        outOfTimeData.nextCycle();
        assertEquals(4, outOfTimeData.getTrainingSet().size());
        validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 5);

        // Verify that we have increased the training set size and moved on to the next validation set
        outOfTimeData.nextCycle();
        assertEquals(5, outOfTimeData.getTrainingSet().size());
        validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 6);

        // Verify that there are no more iterations
        outOfTimeData.nextCycle();
        assertFalse(outOfTimeData.hasMore());
    }

    @Test
    public void testValidateIfThereIsABreakInTheDataWeMoveOnToTheNextPeriod() throws Exception {

        List<ClassifierInstance> instances = new ArrayList<>();

        // Create an instance for 3 days, with a gap after the first two
        instances.add(createClassifierInstance(1));
        instances.add(createClassifierInstance(2));
        instances.add(createClassifierInstance(4));

        OutOfTimeData<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(instances, 0.5, 24, dateTimeExtractor);


        // Verifiy the inital data is split up - half for the training set, and one entry in the validation set
        List<ClassifierInstance> trainingSet = outOfTimeData.getTrainingSet();
        List<ClassifierInstance> validationSet = outOfTimeData.getValidationSet();
        assertEquals(1, trainingSet.size());
        assertDayOfMonthMatches(trainingSet.get(0), 1);
        assertEquals(1, validationSet.size());
        assertDayOfMonthMatches(validationSet.get(0), 2);

        // Verify that we skipped past the missing day and the validation set includes the next day
        outOfTimeData.nextCycle();
        assertEquals(2, outOfTimeData.getTrainingSet().size());
        assertDayOfMonthMatches(outOfTimeData.getValidationSet().get(0), 4);

    }

    private void assertDayOfMonthMatches(final ClassifierInstance instance, final int expected) {
        assertEquals(expected, dateTimeExtractor.extractDateTime(instance).dayOfMonth().get());
    }




}