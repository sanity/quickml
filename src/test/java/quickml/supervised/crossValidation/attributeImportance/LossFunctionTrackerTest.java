package quickml.supervised.crossValidation.attributeImportance;

import org.junit.Before;
import org.junit.Test;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierMSELossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

public class LossFunctionTrackerTest {

    private LossFunctionTracker lossFunctionTracker;
    private ClassifierLossFunction mseLossFunction;
    private ClassifierLossFunction rmseLossFunction;
    private ClassifierLossFunction logCVLossFunction;

    @Before
    public void setUp() throws Exception {
        mseLossFunction = new ClassifierMSELossFunction();
        rmseLossFunction = new ClassifierRMSELossFunction();
        logCVLossFunction = new ClassifierLogCVLossFunction(0.0000001);
    }

    @Test
    public void testLossFunctionsWithAtLeastOneFunctionAreValid() throws Exception {
        lossFunctionTracker = new LossFunctionTracker(newArrayList(mseLossFunction));
        assertEquals(1, lossFunctionTracker.lossFunctionNames().size());
    }

    @Test
    public void testLossFunctionsWithTwoOrMoreAreValid() throws Exception {
        lossFunctionTracker = new LossFunctionTracker(newArrayList(mseLossFunction, rmseLossFunction, logCVLossFunction));
        assertEquals(3, lossFunctionTracker.lossFunctionNames().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyListIsInvalid() throws Exception {
        ArrayList<ClassifierLossFunction> list = newArrayList();
        lossFunctionTracker = new LossFunctionTracker(list);
    }
}