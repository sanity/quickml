package quickml.supervised.classifier;

import org.junit.Test;
import org.testng.Assert;
import org.junit.Before;
import org.junit.Test;
import quickml.ThompsonSampler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;


/**
 * Created by alexanderhawk on 8/13/14.
 */
public class thompsonSamp {
    final Random random = new Random();
    final double maxAdjustedClickProbability = 0.012;
    @Test
    public void testThompson() {
        BigDecimal sampledProb = new BigDecimal(0);
        double maxVal = 0;
        double unsampledProb = 1.5/1000.0;
        int creativeImps=100;
        int campaignImps = 400000;
        double averageSampledProb = 0;
        double numBids = 70;
        for (int bids = 1 ; bids < numBids; bids++ ){
            sampledProb = doThompsonSampling(creativeImps, campaignImps, new BigDecimal(unsampledProb, MathContext.DECIMAL32));
            averageSampledProb += sampledProb.doubleValue();
            if (sampledProb.doubleValue() > maxVal)
                maxVal = sampledProb.doubleValue();
        }
        averageSampledProb/=numBids;
        System.out.println("maxVal: " + maxVal + ". sigma: " + ThompsonSampler.getStandardDeviation(creativeImps,campaignImps, unsampledProb));
        System.out.println("unsampledProb " + unsampledProb + ". average sampled prob: " + averageSampledProb);
        Assert.assertTrue(maxVal > sampledProb.doubleValue());
    }
    private BigDecimal doThompsonSampling(final int creativeImps, int campaignImps, final BigDecimal adjustedClickProbability) {
        double standardDeviation = ThompsonSampler.getStandardDeviation(creativeImps, campaignImps, adjustedClickProbability.doubleValue());
        double conversionProbabilityWithUncertanty = Math.max(0, adjustedClickProbability.doubleValue() + standardDeviation * random.nextGaussian());
        conversionProbabilityWithUncertanty = Math.min(maxAdjustedClickProbability, conversionProbabilityWithUncertanty);
        return new BigDecimal(conversionProbabilityWithUncertanty, MathContext.DECIMAL32);
    }
}
