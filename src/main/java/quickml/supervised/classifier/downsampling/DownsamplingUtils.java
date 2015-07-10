package quickml.supervised.classifier.downsampling;

/**
 * Created by ian on 4/23/14.
 */
public class DownsamplingUtils {
    public static double correctProbability(final double dropProbability, final double uncorrectedProbability) {
        return (1.0 - dropProbability)*uncorrectedProbability / (1.0 - dropProbability * uncorrectedProbability);
    }
}
