package quickml.supervised.inspection;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 12/27/14.
 */
public interface DistributionSampler<V> {
    public V sampleDistribution();
}
