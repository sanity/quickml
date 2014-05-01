package quickdt.predictiveModels.downsamplingPredictiveModel;

import com.beust.jcommander.internal.Lists;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;
import quickdt.Misc;
import quickdt.data.*;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by ian on 4/24/14.
 */
public class DownsamplingPredictiveModelBuilderTest {
    @Test
    public void simpleTest() {
        final PredictiveModelBuilder<?> predictiveModelBuilder = Mockito.mock(PredictiveModelBuilder.class);
        when(predictiveModelBuilder.buildPredictiveModel(Mockito.any(Iterable.class))).thenAnswer(new Answer<PredictiveModel>() {
            @Override
            public PredictiveModel answer(final InvocationOnMock invocationOnMock) throws Throwable {
                Iterable<AbstractInstance> instances = (Iterable<AbstractInstance>) invocationOnMock.getArguments()[0];
                int total = 0, sum = 0;
                for (AbstractInstance instance : instances) {
                    total++;
                    if (instance.getClassification().equals(true)) {
                        sum++;
                    }
                }
                PredictiveModel dumbPM = new SamePredictionPredictiveModel((double) sum / (double) total);
                return dumbPM;
            }
        });
        DownsamplingPredictiveModelBuilder downsamplingPredictiveModelBuilder = new DownsamplingPredictiveModelBuilder(predictiveModelBuilder, 0.2);
        List<Instance> data = Lists.newArrayList();
        for (int x=0; x<10000; x++) {
            data.add(new Instance(new HashMapAttributes(), (Misc.random.nextDouble() < 0.05)));
        }
        PredictiveModel predictiveModel = downsamplingPredictiveModelBuilder.buildPredictiveModel(data);
        final double correctedMinorityInstanceOccurance = predictiveModel.getProbability(new HashMapAttributes(), Boolean.TRUE);
        double error = Math.abs(0.05 - correctedMinorityInstanceOccurance);
        Assert.assertTrue(String.format("Error should be < 0.1 but was %s (prob=%s, desired=0.05)", error, correctedMinorityInstanceOccurance), error < 0.01);
    }

    private static class SamePredictionPredictiveModel implements PredictiveModel {

        private static final long serialVersionUID = 8241616760952568181L;
        private final double prediction;

        public SamePredictionPredictiveModel(double prediction) {

            this.prediction = prediction;
        }

        @Override
        public double getProbability(final Attributes attributes, final Serializable classification) {
            return prediction;
        }

        @Override
        public void dump(final PrintStream printStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Serializable getClassificationByMaxProb(final Attributes attributes) {
            return null;
        }
    }
}

