package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import com.beust.jcommander.internal.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MonotonicConvergenceRecommenderTest {

    private MonotonicConvergenceRecommender recommender;

    @Before
    public void setUp() throws Exception {
        recommender = new MonotonicConvergenceRecommender(Arrays.asList(1, 5, 10, 20, 40));
    }


    @Test
    public void testWeStopIfThresholdIsNotReached() throws Exception {
        List<Double> losses = Lists.newArrayList();
        for (int i = 0; i < recommender.getValues().size(); i++) {
            losses.add(Math.random());
            if (!recommender.shouldContinue(losses))
                break;
        }

        assertEquals(5, losses.size());
    }


    @Ignore("Need to test a scenario where we break")
    @Test
    public void testWeContinueIfWeHaventGoneOverTheTolerance() throws Exception {
        List<Double> losses = Lists.newArrayList();

        for (int i = 1; i <= recommender.getValues().size(); i++) {
            losses.add(i * 0.001d);
            if (!recommender.shouldContinue(losses))
                break;
        }

        System.out.println("losses = " + losses);
        assertEquals(2, losses.size());

    }
}