package quickml;

public class ThompsonSampler {

    /**
     * The below constant incorporates a correction for the fact that not all of the samples we have collected for a
     * creative are relevant to to the context of the impression.  A good approximation for this correction factor
     * would be 2^(the mean depth of a decision tree), as that is how much data we use for any particular context.
     * <p/>
     * However, this correction would be too aggressive because data from other other creatives in the same campaign
     * and cross campaign data is used in our model to decrease the uncertainty of our predictions.
     * <p/>
     * I settled on the value of 5 for the time being because this appeared to give reasonable values of the standard
     * deviation as a function of the the number impressions and the predicted ctr.
     * It is not an optimal choice...hopefully it is good enough for the time being.
     */

    private static final double CORRECTION_FOR_CONTEXT_AND_SUPPORTING_DATA = 2.0;
    private static final double TYPICAL_NUMBER_OF_DESTINATIONS_FOR_A_CAMPAIGN = 20.0;

    public static double getStandardDeviation(int impsForCreative, int impressionsForCampaign, double ctr) {
        double proxyImpressions = Math.min(impsForCreative, impressionsForCampaign / TYPICAL_NUMBER_OF_DESTINATIONS_FOR_A_CAMPAIGN);
        double nEffective = ((impsForCreative + proxyImpressions) / CORRECTION_FOR_CONTEXT_AND_SUPPORTING_DATA) + 1;
        return Math.sqrt((ctr - (ctr * ctr)) / nEffective);
    }
}
