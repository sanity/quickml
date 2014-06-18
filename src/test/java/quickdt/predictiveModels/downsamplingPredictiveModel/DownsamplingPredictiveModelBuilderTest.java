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
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.calibratedPredictiveModel.PAVCalibratedPredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    @Test
    public void simpleBmiTest() throws IOException, ClassNotFoundException {
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder urfb = new RandomForestBuilder(tb);
        final DownsamplingPredictiveModelBuilder dpmb = new DownsamplingPredictiveModelBuilder(urfb, 0.1);

        final List<Instance> instances = TreeBuilderTestUtils.getIntegerInstances(1000);
        final PredictiveModelWithDataBuilder<DownsamplingPredictiveModel> wb = new PredictiveModelWithDataBuilder<>(dpmb);
        final long startTime = System.currentTimeMillis();
        final DownsamplingPredictiveModel downsamplingPredictiveModel = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(downsamplingPredictiveModel);

        RandomForest randomForest = (RandomForest) downsamplingPredictiveModel.wrappedPredictiveModel;
        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final int firstTreeNodeSize = trees.get(0).node.size();
        org.testng.Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        org.testng.Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000, "Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getIntegerInstances(1000);
        final DownsamplingPredictiveModel downsamplingPredictiveModel1 = wb.buildPredictiveModel(newInstances);
        final RandomForest newRandomForest = (RandomForest) downsamplingPredictiveModel1.wrappedPredictiveModel;
        org.testng.Assert.assertTrue(downsamplingPredictiveModel == downsamplingPredictiveModel1, "Expect same tree to be updated");
        org.testng.Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        org.testng.Assert.assertEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected same nodes");
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
        public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
            throw new UnsupportedOperationException();
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

