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
 * This strategy will inject new attributes for a particular set of existing attributes corresponding to
 * the probability of a specified classification given the value associated with that attribute.  So, for example,
 * if we are predicting a person's likelihood to have an illness based on a variety of factors including gender,
 * and a generic male's overall probability of having the illness is 0.2 based on our training data, then it will
 * enrich with an attribute like "male-PROB"=0.2.
 */
public class ProbabilityEnrichStrategy implements AttributesEnrichStrategy {

    private static final int DEFAULT_MAX_VALUE_COUNT = 20000;

    private final Set<String> attributeKeysToInject;
    private final Serializable classification;
    private final int maxValueCount;

    /**
     *
     * @param attributeKeysToInject The attributes to enrich with probabilities
     * @param classification The classification whose probability we should use.  If there are only two
     *                       classifications then it doesn't particularly matter which one we use.  If there
     *                       are more than two you might wish to create multiple enrich strategies, each
     *                       looking at a different classification.
     */
    public ProbabilityEnrichStrategy(PredictiveModelBuilder<?> wrappedBuilder, Set<String> attributeKeysToInject, Serializable classification) {
        this(attributeKeysToInject, classification, DEFAULT_MAX_VALUE_COUNT);
    }

    /**
     * @param attributeKeysToInject The attributes to enrich with probabilities
     * @param classification The classification whose probability we should use.  If there are only two
     *                       classifications then it doesn't particularly matter which one we use.  If there
     *                       are more than two you might wish to create multiple enrich strategies, each
     *                       looking at a different classification.
     * @param maxValueCount This is the maximum number of values an attribute can have before it will be
     *                      ignored by ProbabilityEnrichStrategy.  If unspecified the default is 20,000.
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
