package quickml.supervised.featureEngineering;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;
import quickml.data.*;

import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureEngineeringPredictiveModelBuilderTest {

    private static Integer valueToTest = 1;

    @Test
    public void simpleTest() {
        TestAEBS testFEPMB = new TestAEBS();
        List<Instance<Map<String, Serializable>>> trainingData = Lists.newArrayList();
        trainingData.add(new InstanceImpl(new HashMap(), 1));
        PredictiveModelBuilder testPMB = new TestPMBuilder();
        FeatureEngineeringPredictiveModelBuilder feBuilder = new FeatureEngineeringPredictiveModelBuilder(testPMB, Lists.newArrayList(new TestAEBS()));
        final FeatureEngineeredPredictiveModel predictiveModel = feBuilder.buildPredictiveModel(trainingData);
        predictiveModel.getProbability(trainingData.get(0).getRegressors(), valueToTest);
    }

    public static class TestAEBS implements AttributesEnrichStrategy {

        @Override
        public AttributesEnricher build(final Iterable<? extends Instance<Map<String, Serializable>>> trainingData) {
            return new AttributesEnricher() {
                private static final long serialVersionUID = -4851048617673142530L;

                public Map<String, Serializable> apply(@Nullable final Map<String, Serializable> attributes) {
                    Map<String, Serializable> er = new HashMap<>();
                    er.putAll(attributes);
                    er.put("enriched", 1);
                    return er;
                }
            };
        }
    }

    public static class TestPMBuilder implements PredictiveModelBuilder<Map<String, Serializable>, TestPM> {

        @Override
        public TestPM buildPredictiveModel(Iterable<? extends Instance<Map<String, Serializable>>> trainingData) {
            for (Instance<Map<String, Serializable>> instance : trainingData) {
                if (!instance.getRegressors().containsKey("enriched")) {
                    throw new IllegalArgumentException("Predictive model training data must contain enriched instances");
                }
            }

            return new TestPM();
        }

        @Override
        public TestPMBuilder updatable(boolean updatable) {
            return this;
        }

        @Override
        public void setID(Serializable id) {

        }

    }


    public static class TestPM implements PredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero> {


            private static final long serialVersionUID = -3449746370937561259L;

            @Override
            public MapWithDefaultOfZero predict(Map<String, Serializable> regressors) {
                if (!regressors.containsKey("enriched")) {
                    throw new IllegalArgumentException("Predictive model training data must contain enriched instances");
                }
                Map<Serializable, Double> map = new HashMap<>();
                map.put(valueToTest, 0.0);
                return new MapWithDefaultOfZero(map);
            }

            @Override
            public void dump(Appendable appendable) {

            }
        }
    }


