package quickdt;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class Misc {
	public static final Random random = new Random();
	
    /**
     * <p>
     * Simple implementation of a bagging predictor using multiple decision trees.
     * The idea is to create a random bootstrap sample of the training data to grow
     * multiple trees. For more information see <a
     * href="http://www.stat.berkeley.edu/tech-reports/421.pdf">Bagging
     * Predictors</a>, Leo Breiman, 1994.
     * </p>
     *
     * Bagging code taken from contribution by Philipp Katz
     */
    public static List<AbstractInstance> getBootstrapSampling(Iterable <? extends AbstractInstance> trainingData) {
        final List<? extends AbstractInstance> allInstances = Lists.newArrayList(trainingData);
        final List<AbstractInstance> sampling = Lists.newArrayList();
        for (int i = 0; i < allInstances.size(); i++) {
            int sample = Misc.random.nextInt(allInstances.size());
            sampling.add(allInstances.get(sample));
        }
        return sampling;
    }

}
