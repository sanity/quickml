package quickml.supervised.classifier.downsampling;

import com.beust.jcommander.internal.Lists;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;
import quickml.collections.MapUtils;
import quickml.data.*;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Created by ian on 4/24/14.
 */
public class DownsamplingClassifierBuilderTest {
    @Test
    public void simpleTest() {
        final PredictiveModelBuilder<Map<String,Serializable>, Classifier> predictiveModelBuilder = Mockito.mock(PredictiveModelBuilder.class);
        when(predictiveModelBuilder.buildPredictiveModel(Mockito.any(Iterable.class))).thenAnswer(new Answer<Classifier>() {
            @Override
            public Classifier answer(final InvocationOnMock invocationOnMock) throws Throwable {
                Iterable<Instance<Map<String, Serializable>>> instances = (Iterable<Instance<Map<String, Serializable>>>) invocationOnMock.getArguments()[0];
                int total = 0, sum = 0;
                for (Instance<Map<String, Serializable>> instance : instances) {
                    total++;
                    if (instance.getLabel().equals(true)) {
                        sum++;
                    }
                }
                Classifier dumbPM = new SamePredictionPredictiveModel((double) sum / (double) total);
                return dumbPM;
            }
        });
        DownsamplingClassifierBuilder downsamplingClassifierBuilder = new DownsamplingClassifierBuilder(predictiveModelBuilder, 0.2);
        List<Instance<Map<String,Serializable>>> data = Lists.newArrayList();
        for (int x=0; x<10000; x++) {
            data.add(new InstanceImpl(new HashMap(), (MapUtils.random.nextDouble() < 0.05)));
        }
        DownsamplingClassifier predictiveModel = downsamplingClassifierBuilder.buildPredictiveModel(data);
        Map<String,Serializable> map = new HashMap<>();
        map.put("true",Boolean.TRUE);
        final double correctedMinorityInstanceOccurance = predictiveModel.getProbability(map, Boolean.TRUE);
        double error = Math.abs(0.05 - correctedMinorityInstanceOccurance);
        Assert.assertTrue(String.format("Error should be < 0.1 but was %s (prob=%s, desired=0.05)", error, correctedMinorityInstanceOccurance), error < 0.01);
    }

    @Test
    public void simpleBmiTest() throws IOException, ClassNotFoundException {
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder urfb = new RandomForestBuilder(tb);
        final DownsamplingClassifierBuilder dpmb = new DownsamplingClassifierBuilder((PredictiveModelBuilder)urfb, 0.1);

        final List<Instance<Map<String,Serializable>>> instances = TreeBuilderTestUtils.getIntegerInstances(1000);
        final PredictiveModelWithDataBuilder<Map<String,Serializable>,DownsamplingClassifier> wb = new PredictiveModelWithDataBuilder<>(dpmb);
        final long startTime = System.currentTimeMillis();
        final DownsamplingClassifier downsamplingClassifier = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(downsamplingClassifier);

        RandomForest randomForest = (RandomForest) downsamplingClassifier.wrappedClassifier;
        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final int firstTreeNodeSize = trees.get(0).node.size();
        org.testng.Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        org.testng.Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000, "Building this node should take far less than 20 seconds");

        final List<Instance<Map<String,Serializable>>> newInstances = TreeBuilderTestUtils.getIntegerInstances(1000);
        final DownsamplingClassifier downsamplingClassifier1 = wb.buildPredictiveModel(newInstances);
        final RandomForest newRandomForest = (RandomForest) downsamplingClassifier1.wrappedClassifier;
        org.testng.Assert.assertTrue(downsamplingClassifier == downsamplingClassifier1, "Expect same tree to be updated");
        org.testng.Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        org.testng.Assert.assertEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected same nodes");
    }

    private static class SamePredictionPredictiveModel extends Classifier {

        private static final long serialVersionUID = 8241616760952568181L;
        private final double prediction;

        public SamePredictionPredictiveModel(double prediction) {

            this.prediction = prediction;
        }

        @Override
        public void dump(final Appendable appendable) {
            throw new UnsupportedOperationException();
        }


        @Override
        public MapWithDefaultOfZero predict(Map<String, Serializable> attributes) {
            Map<Serializable, Double> map = new HashMap<>();
            for(Serializable value : attributes.values()) {
                map.put(value, prediction);
            }
            return new MapWithDefaultOfZero(map);
        }
    }
}

