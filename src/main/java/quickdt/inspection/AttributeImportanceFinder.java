package quickdt.inspection;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.uprizer.sensearray.freetools.stats.ReservoirSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.*;
import quickdt.experiments.crossValidation.CrossValidator;

import java.io.Serializable;
import java.util.*;

public class AttributeImportanceFinder {
    private static final  Logger logger =  LoggerFactory.getLogger(AttributeImportanceFinder.class);

    public AttributeImportanceFinder() {

    }

    public TreeSet<AttributeScore> determineAttributeImportance(final Iterable<AbstractInstance> trainingData) {
        return determineAttributeImportance(new TreeBuilder(), trainingData);
    }


    public TreeSet<AttributeScore> determineAttributeImportance(PredictiveModelBuilder predictiveModelBuilder, final Iterable<AbstractInstance> trainingData) {
        return determineAttributeImportance(new CrossValidator(4, 1), predictiveModelBuilder, trainingData);
    }

    public TreeSet<AttributeScore> determineAttributeImportance(CrossValidator crossValidator, PredictiveModelBuilder predictiveModelBuilder, final Iterable<AbstractInstance> trainingData) {

        Set<String> attributes = Sets.newHashSet();
        for (AbstractInstance instance : trainingData) {
            attributes.addAll(instance.getAttributes().keySet());
        }

        TreeSet<AttributeScore> scores = Sets.newTreeSet();

        LinkedList<AbstractInstance> trainingSet = Lists.newLinkedList();
        LinkedList<AbstractInstance> testingSet = Lists.newLinkedList();
        for (AbstractInstance instance : trainingData) {
            if (Math.abs(instance.getAttributes().hashCode()) % 10 == 0) {
                testingSet.add(instance);
            } else {
                trainingSet.add(instance);
            }
        }

        Map<String, ReservoirSampler<Serializable>> samplesPerAttribute = Maps.newHashMap();
        for (AbstractInstance instance : trainingData) {
            for (Map.Entry<String,Serializable> attributeKeyValue : instance.getAttributes().entrySet()) {
                ReservoirSampler<Serializable> sampler = samplesPerAttribute.get(attributeKeyValue.getKey());
                if (sampler == null) {
                    sampler = new ReservoirSampler<Serializable>(1000);
                    samplesPerAttribute.put(attributeKeyValue.getKey(), sampler);
                }
                sampler.addSample(attributeKeyValue.getValue());
            }
        }

        for (String attributeToExclude : attributes) {
            final ReservoirSampler<Serializable> samplerForAttributeToExclude = samplesPerAttribute.get(attributeToExclude);
            final ArrayList<Serializable> samplesForAttribute = Lists.newArrayList(samplerForAttributeToExclude.getSamples());
            if (samplesForAttribute.size() < 2) continue;
            Iterable<AbstractInstance> scrambledTestingSet = Lists.newLinkedList(Iterables.transform(testingSet, new AttributeScrambler(attributeToExclude, samplesForAttribute)));
            double score = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);
            logger.info("Attribute \""+attributeToExclude+"\" score is "+score);
            scores.add(new AttributeScore(attributeToExclude, score));
        }

        return scores;
    }

    public static class AttributeScrambler implements Function<AbstractInstance, AbstractInstance> {

        public AttributeScrambler(final String attributeToExclude, ArrayList<Serializable> attributeValueSamples) {
            this.attributeToExclude = attributeToExclude;
            this.attributeValueSamples = attributeValueSamples;
        }

        private final String attributeToExclude;
        private final ArrayList<Serializable> attributeValueSamples;

        @Override
        public AbstractInstance apply(final AbstractInstance instance) {
            Attributes randomizedAttributes = new HashMapAttributes();
            randomizedAttributes.putAll(instance.getAttributes());
            final Serializable randomValue = attributeValueSamples.get(Misc.random.nextInt(Math.max(1, attributeValueSamples.size())));
            randomizedAttributes.put(attributeToExclude, randomValue);
            return new Instance(randomizedAttributes, instance.getClassification());
        }
    }

 }