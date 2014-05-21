package quickdt.predictiveModels.featureEngineering.enrichStrategies.probabilityInjector;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.featureEngineering.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by ian on 5/19/14.
 */
public class ProbabilityEnrichStrategy implements AttributesEnricherBuildStrategy {

    private static final int DEFAULT_MAX_VALUE_COUNT = 20000;

    private final Set<String> attributeKeysToInject;
    private final Serializable classification;
    private final int maxValueCount;

    /**
     *
     * @param attributeKeysToInject
     * @param classification
     */
    public ProbabilityEnrichStrategy(PredictiveModelBuilder<?> wrappedBuilder, Set<String> attributeKeysToInject, Serializable classification) {
        this(attributeKeysToInject, classification, DEFAULT_MAX_VALUE_COUNT);
    }

    /**
     *  @param attributeKeysToInject
     * @param classification
     * @param maxValueCount
     */
    public ProbabilityEnrichStrategy(Set<String> attributeKeysToInject, Serializable classification, final int maxValueCount) {
        this.attributeKeysToInject = attributeKeysToInject;
        this.classification = classification;
        this.maxValueCount = maxValueCount;
    }

    @Override
    public AttributesEnricher build(final Iterable<? extends AbstractInstance> trainingData) {
        Map<String, Map<Serializable, ProbCounter>> valueProbCountersByAttribute = Maps.newHashMap();

        Set<String> attributesWithTooManyValues = Sets.newHashSet();

        for (AbstractInstance instance : trainingData) {
            int classificationMatch = instance.getClassification().equals(classification) ? 1 : 0;
            for (String attributeKey : attributeKeysToInject) {
                if (attributesWithTooManyValues.contains(attributeKey)) {
                    continue;
                }

                Map<Serializable, ProbCounter> attributeValueProbabilities = valueProbCountersByAttribute.get(attributeKey);
                if (attributeValueProbabilities == null) {
                    attributeValueProbabilities = Maps.newHashMap();
                    valueProbCountersByAttribute.put(attributeKey, attributeValueProbabilities);
                }
                if (attributeValueProbabilities.size() > maxValueCount) {
                    attributesWithTooManyValues.add(attributeKey);
                    valueProbCountersByAttribute.remove(attributeKey);
                    continue;
                }
                Serializable value = instance.getAttributes().get(attributeKey);
                if (value == null) {
                    value = Integer.MIN_VALUE;
                }
                ProbCounter probCounter = attributeValueProbabilities.get(value);
                if (probCounter == null) {
                    probCounter = new ProbCounter();
                    attributeValueProbabilities.put(value, probCounter);
                }
                probCounter.add(classificationMatch, instance.getWeight());
            }
        }

        Map<String, Map<Serializable, Double>> attributeValueProbabilitiesByAttribute = Maps.newHashMap();

        for (Map.Entry<String, Map<Serializable, ProbCounter>> attributeValueProbEntry : valueProbCountersByAttribute.entrySet()) {
            Map<Serializable, Double> probabilitiesByValue = Maps.newHashMap();

            for (Map.Entry<Serializable, ProbCounter> valueProbEntry : attributeValueProbEntry.getValue().entrySet()) {
                probabilitiesByValue.put(valueProbEntry.getKey(), valueProbEntry.getValue().getProb());
            }
            attributeValueProbabilitiesByAttribute.put(attributeValueProbEntry.getKey(), probabilitiesByValue);
        }

        return new ProbabilityInjectingEnricher(attributeValueProbabilitiesByAttribute);
    }

    /**
     * Keeps a running average of the classificationMatch value, weighted accordingly
     */
    private static class ProbCounter {
        private double sum = 0;
        private double total = 0;

        public void add(int classificationMatch, double weight) {
            sum += classificationMatch * weight;
            total += weight;
        }

        public double getProb() {
            return sum / total;
        }
    }
}
