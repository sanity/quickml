package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.collect.Lists;
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
        recommender = new MonotonicConvergenceRecommender(Arrays.asList(1, 5, 10, 20, 40), 0.1);
    }


    @Test
    public void testWeStopIfThresholdIsNotReached() throws Exception {
        List<Double> losses = Lists.newArrayList();
        for (int i = 0; i < recommender.getValues().size(); i++) {
            double prevLoss = (i>0) ? losses.get(i-1) : 1.0;
            losses.add(prevLoss*2);
            if (!recommender.shouldContinue(losses))
                break;
        }
//
        assertEquals(5, losses.size());
    }


    @Test
    public void testWeContinueIfWeHaventGoneOverTheTolerance() throws Exception {
        List<Double> losses = Lists.newArrayList();
        double[] lossValue = new double[]{0.001, 0.002, 0.002001, 0.004, 0.005};
        for (int i = 0; i < recommender.getValues().size(); i++) {
            losses.add(lossValue[i]);
            if (!recommender.shouldContinue(losses))
                break;
        }

        System.out.println("losses = " + losses);
        assertEquals(3, losses.size());

    }
}