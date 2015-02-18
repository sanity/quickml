package quickml.supervised.classifier.downsampling;

import com.beust.jcommander.internal.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;
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
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ian on 4/24/14.
 */
public class DownsamplingClassifierBuilderTest {
    @Test
    public void simpleTest() {
        PredictiveModelBuilder mockPredictiveModelBuilder = mock(PredictiveModelBuilder.class);
        when(mockPredictiveModelBuilder.buildPredictiveModel(Mockito.any(Iterable.class))).thenAnswer(new Answer<Classifier>() {
            @Override
            public Classifier answer(final InvocationOnMock invocationOnMock) throws Throwable {
                Iterable<Instance<AttributesMap, Serializable>> instances = (Iterable<Instance<AttributesMap, Serializable>>) invocationOnMock.getArguments()[0];
                int total = 0, sum = 0;
                for (Instance<AttributesMap, Serializable> instance : instances) {
                    total++;
                    if (instance.getLabel().equals(true)) {
                        sum++;
                    }
                }
                Classifier dumbPM = new SamePredictionPredictiveModel((double) sum / (double) total);
                return dumbPM;
            }
        });
        DownsamplingClassifierBuilder downsamplingClassifierBuilder = new DownsamplingClassifierBuilder(mockPredictiveModelBuilder, 0.2);
        List<ClassifierInstance> data = Lists.newArrayList();
        for (int x = 0; x < 10000; x++) {
            data.add(new ClassifierInstance(AttributesMap.newHashMap(), (MapUtils.random.nextDouble() < 0.05)));
        }
        DownsamplingClassifier predictiveModel = downsamplingClassifierBuilder.buildPredictiveModel(data);
        AttributesMap map = AttributesMap.newHashMap();
        map.put("true", Boolean.TRUE);
        final double correctedMinorityInstanceOccurance = predictiveModel.getProbability(map, Boolean.TRUE);
        double error = Math.abs(0.05 - correctedMinorityInstanceOccurance);
        assertTrue(String.format("Error should be < 0.1 but was %s (prob=%s, desired=0.05)", error, correctedMinorityInstanceOccurance), error < 0.01);
    }

    @Test
    public void simpleBmiTest() throws IOException, ClassNotFoundException {
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder urfb = new RandomForestBuilder(tb);
        final DownsamplingClassifierBuilder dpmb = new DownsamplingClassifierBuilder(urfb, 0.1);

        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getIntegerInstances(1000);
        final long startTime = System.currentTimeMillis();
        final DownsamplingClassifier downsamplingClassifier = dpmb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(downsamplingClassifier);

        RandomForest randomForest = (RandomForest) downsamplingClassifier.wrappedClassifier;
        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final int firstTreeNodeSize = trees.get(0).node.size();
        org.testng.Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        org.testng.Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000, "Building this node should take far less than 20 seconds");
    }

    private static class SamePredictionPredictiveModel extends AbstractClassifier {

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
        public PredictionMap predict(AttributesMap attributes) {
            Map<Serializable, Double> map = new HashMap<>();
            for (Serializable value : attributes.values()) {
                map.put(value, prediction);
            }
            return new PredictionMap(map);
        }

        @Override
        public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
            return predict(attributes);
        }
    }
}
