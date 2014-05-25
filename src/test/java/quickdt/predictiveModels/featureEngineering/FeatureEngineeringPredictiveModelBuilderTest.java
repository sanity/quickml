package quickdt.predictiveModels.featureEngineering;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;
import quickdt.data.*;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FeatureEngineeringPredictiveModelBuilderTest {

    @Test
    public void simpleTest() {
        TestAEBS testFEPMB = new TestAEBS();
        List<Instance> trainingData = Lists.newArrayList();
        trainingData.add(new Instance(new HashMapAttributes(), 1));
        FeatureEngineeringPredictiveModelBuilder feBuilder = new FeatureEngineeringPredictiveModelBuilder(new TestPMBuilder(), Lists.newArrayList(new TestAEBS()));
        final FeatureEngineeredPredictiveModel predictiveModel = feBuilder.buildPredictiveModel(trainingData);
        predictiveModel.getProbability(trainingData.get(0).getAttributes(), 1);
    }

    public static class TestAEBS implements AttributesEnrichStrategy {

        @Override
        public AttributesEnricher build(final Iterable<? extends AbstractInstance> trainingData) {
            return new AttributesEnricher() {
                private static final long serialVersionUID = -4851048617673142530L;

                @Nullable
                @Override
                public Attributes apply(@Nullable final Attributes attributes) {
                    HashMapAttributes er = new HashMapAttributes();
                    er.putAll(attributes);
                    er.put("enriched", 1);
                    return er;
                }
            };
        }

    }

    public static class TestPMBuilder implements PredictiveModelBuilder<TestPM> {

        @Override
        public TestPM buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
            for (AbstractInstance instance : trainingData) {
                if (!instance.getAttributes().containsKey("enriched")) {
                    throw new IllegalArgumentException("Predictive model training data must contain enriched instances");
                }
            }

            return new TestPM();
        }
    }

    public static class TestPM implements PredictiveModel {

        private static final long serialVersionUID = -3449746370937561259L;

        @Override
        public double getProbability(final Attributes attributes, final Serializable classification) {
            if (!attributes.containsKey("enriched")) {
                throw new IllegalArgumentException("Predictive model training data must contain enriched instances");
            }
            return 0;
        }

        @Override
        public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void dump(final PrintStream printStream) {

        }

        @Override
        public Serializable getClassificationByMaxProb(final Attributes attributes) {
            return null;
        }
    }

}

