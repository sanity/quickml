package quickdt.inspection;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.twitter.common.stats.ReservoirSampler;
import quickdt.*;

import java.io.Serializable;
import java.util.*;

public class AttributeImportanceFinder {
    public AttributeImportanceFinder() {

    }

    public TreeSet<AttributeScore> determineAttributeImportance(PredictiveModel predictiveModel, final Iterable<Instance> clickTrainingInstances) {
        Set<String> attributes = Sets.newHashSet();
        for (Instance instance : clickTrainingInstances) {
            attributes.addAll(instance.getAttributes().keySet());
        }

        ScoreFactory scoreFactory = new RMSEScoreFactory();
        TreeSet<AttributeScore> scores = Sets.newTreeSet();

        LinkedList<Instance> trainingSet = Lists.newLinkedList();
        LinkedList<Instance> testingSet = Lists.newLinkedList();
        for (Instance instance : clickTrainingInstances) {
            if (Math.abs(instance.getAttributes().hashCode()) % 10 == 0) {
                testingSet.add(instance);
            } else {
                trainingSet.add(instance);
            }
        }

        Map<String, ReservoirSampler<Serializable>> samplesPerAttribute = Maps.newHashMap();
        for (Instance instance : clickTrainingInstances) {
            for (Map.Entry<String,Serializable> attributeKeyValue : instance.getAttributes().entrySet()) {
                ReservoirSampler<Serializable> sampler = samplesPerAttribute.get(attributeKeyValue.getKey());
                if (sampler == null) {
                    sampler = new ReservoirSampler<Serializable>(1000);
                    samplesPerAttribute.put(attributeKeyValue.getKey(), sampler);
                }
                sampler.sample(attributeKeyValue.getValue());
            }
        }

        for (String attributeToExclude : attributes) {
            final ReservoirSampler<Serializable> samplerForAttributeToExclude = samplesPerAttribute.get(attributeToExclude);
            final ArrayList<Serializable> samplesForAttribute = Lists.newArrayList(samplerForAttributeToExclude.getSamples());
            if (samplesForAttribute.size() < 2) continue;
            Iterable<Instance> scrambledTestingSet = Iterables.transform(testingSet, new AttributeScrambler(attributeToExclude, samplesForAttribute));
            Score score = scoreFactory.get();
            for (Instance testInstance : scrambledTestingSet) {
                score.addResult(testInstance, predictiveModel);
            }
            scores.add(new AttributeScore(attributeToExclude, score));
        }

        return scores;
    }

    public static class AttributeScrambler implements Function<Instance, Instance> {

        public AttributeScrambler(final String attributeToExclude, ArrayList<Serializable> attributeValueSamples) {
            this.attributeToExclude = attributeToExclude;
            this.attributeValueSamples = attributeValueSamples;
        }

        private final String attributeToExclude;
        private final ArrayList<Serializable> attributeValueSamples;

        @Override
        public Instance apply(final quickdt.Instance instance) {
            Attributes randomizedAttributes = new HashMapAttributes();
            randomizedAttributes.putAll(instance.getAttributes());
            final Serializable randomValue = attributeValueSamples.get(Misc.random.nextInt(Math.max(1, attributeValueSamples.size())));
            randomizedAttributes.put(attributeToExclude, randomValue);
            return new Instance(randomizedAttributes, instance.getClassification());
        }
    }

    public static interface ScoreFactory extends Supplier<Score> {

    }

    public static class RMSEScoreFactory implements ScoreFactory {

        @Override
        public Score get() {
            return new RMSEScore();
        }
    }

    public static interface Score extends Comparable<Score> {
        public void addResult(Instance instance, PredictiveModel predictiveModel);

        public String toString();
    }

    public static class AttributeScore implements Comparable<AttributeScore> {
        public String attribute;
        public Score score;

        public AttributeScore(final String attribute, final Score score) {
            this.attribute = attribute;
            this.score = score;
        }

        @Override
        public int compareTo(final AttributeScore o) {
            int s = score.compareTo(o.score);
            if (s != 0) return s;
            else return attribute.compareTo(o.attribute);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("AttributeScore{");
            sb.append("attribute='").append(attribute).append('\'');
            sb.append(", score=").append(score);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class RMSEScore implements Score {
        double squaredError = 0;
        int count = 0;
        double predictionSum = 0, predictionSquaredSum = 0;

        public double getRMSE() {
            return Math.sqrt(squaredError / count);
        }

        public double getPredictionStdDev() {
            return Math.sqrt((predictionSquaredSum - predictionSum*predictionSum/count)/(count-1.0));
        }

        @Override
        public void addResult(final Instance instance, final PredictiveModel predictiveModel) {
            count++;
            final double prediction = predictiveModel.getProbability(instance.getAttributes(), instance.getClassification());
            double error = 1.0 - prediction;
            squaredError += error*error;
            predictionSum += prediction;
            predictionSquaredSum += prediction*prediction;
        }

        @Override
        public int compareTo(final Score o) {
            // We reverse this because a lower RMSE is better
            return  - Double.compare(getRMSE(), ((RMSEScore) o).getRMSE());
        }

        @Override
        public String toString() {
            return Double.toString(getRMSE());
        }
    }
 }