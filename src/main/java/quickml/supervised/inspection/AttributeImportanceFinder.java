package quickml.supervised.inspection;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.twitter.common.stats.ReservoirSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.collections.MapUtils;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.StationaryCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierLogCVLossFunction;
import quickml.data.*;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.TreeBuilder;

import java.io.Serializable;
import java.util.*;

public class AttributeImportanceFinder {
    private static final  Logger logger =  LoggerFactory.getLogger(AttributeImportanceFinder.class);

    public AttributeImportanceFinder() {

    }

    public TreeSet<AttributeScore> determineAttributeImportance(final Iterable<? extends Instance<AttributesMap>> trainingData) {
        return determineAttributeImportance(new TreeBuilder(), trainingData);
    }


    public TreeSet<AttributeScore> determineAttributeImportance(PredictiveModelBuilder predictiveModelBuilder, final Iterable<? extends Instance<AttributesMap>> trainingData) {
        return determineAttributeImportance(new StationaryCrossValidator(4, new ClassifierLogCVLossFunction()), predictiveModelBuilder, trainingData);
    }

    public TreeSet<AttributeScore> determineAttributeImportance(CrossValidator<AttributesMap, PredictiveModel> crossValidator, PredictiveModelBuilder predictiveModelBuilder, final Iterable<? extends Instance<AttributesMap>> trainingData) {

        Set<String> attributes = Sets.newHashSet();
        for (Instance<AttributesMap> instance : trainingData) {
            attributes.addAll(instance.getAttributes().keySet());
        }

        TreeSet<AttributeScore> scores = Sets.newTreeSet();

        LinkedList<Instance<AttributesMap>> trainingSet = Lists.newLinkedList();
        LinkedList<Instance<AttributesMap>> testingSet = Lists.newLinkedList();
        for (Instance<AttributesMap> instance : trainingData) {
            if (Math.abs(instance.getAttributes().hashCode()) % 10 == 0) {
                testingSet.add(instance);
            } else {
                trainingSet.add(instance);
            }
        }

        Map<String, ReservoirSampler<Serializable>> samplesPerAttribute = Maps.newHashMap();
        for (Instance<AttributesMap> instance : trainingData) {
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
            Iterable<? extends Instance<AttributesMap>> scrambledTestingSet = Lists.newLinkedList(Iterables.transform(testingSet, new AttributeScrambler(attributeToExclude, samplesForAttribute)));
            double score = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, scrambledTestingSet);
            logger.info("Attribute \""+attributeToExclude+"\" score is "+score);
            scores.add(new AttributeScore(attributeToExclude, score));
        }

        return scores;
    }

    public static class AttributeScrambler implements Function<Instance<AttributesMap>, Instance<AttributesMap>> {

        public AttributeScrambler(final String attributeToExclude, ArrayList<Serializable> attributeValueSamples) {
            this.attributeToExclude = attributeToExclude;
            this.attributeValueSamples = attributeValueSamples;
        }

        private final String attributeToExclude;
        private final ArrayList<Serializable> attributeValueSamples;

        public Instance<AttributesMap> apply(final Instance<AttributesMap> instance) {
            AttributesMap randomizedAttributes = AttributesMap.newHashMap();
            randomizedAttributes.putAll(instance.getAttributes());
            final Serializable randomValue = attributeValueSamples.get(MapUtils.random.nextInt(attributeValueSamples.size()));
            randomizedAttributes.put(attributeToExclude, randomValue);
            return new InstanceImpl(randomizedAttributes, instance.getLabel());
        }
    }

 }