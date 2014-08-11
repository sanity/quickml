package quickdt.inspection;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.twitter.common.stats.ReservoirSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Misc;
import quickdt.crossValidation.CrossValidator;
import quickdt.crossValidation.StationaryCrossValidator;
import quickdt.data.*;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

public class AttributeImportanceFinder {
    private static final  Logger logger =  LoggerFactory.getLogger(AttributeImportanceFinder.class);

    public AttributeImportanceFinder() {

    }

    public TreeSet<AttributeScore> determineAttributeImportance(final Iterable<Instance<Map<String, Serializable>>> trainingData) {
        return determineAttributeImportance(new TreeBuilder(), trainingData);
    }


    public TreeSet<AttributeScore> determineAttributeImportance(PredictiveModelBuilder<? extends PredictiveModel<Object>> predictiveModelBuilder, final Iterable<Instance> trainingData) {
        return determineAttributeImportance(new StationaryCrossValidator(4, 1), predictiveModelBuilder, trainingData);
    }

    public TreeSet<AttributeScore> determineAttributeImportance(CrossValidator<PredictiveModel> crossValidator, PredictiveModelBuilder predictiveModelBuilder, final Iterable<Instance> trainingData) {

        Set<String> attributes = Sets.newHashSet();
        for (Instance<Map<String, Serializable>> instance : trainingData) {
            attributes.addAll(instance.getRegressors().keySet());
        }

        TreeSet<AttributeScore> scores = Sets.newTreeSet();

        LinkedList<Instance> trainingSet = Lists.newLinkedList();
        LinkedList<Instance> testingSet = Lists.newLinkedList();
        for (Instance<Map<String, Serializable>> instance : trainingData) {
            if (Math.abs(instance.getRegressors().hashCode()) % 10 == 0) {
                testingSet.add(instance);
            } else {
                trainingSet.add(instance);
            }
        }

        Map<String, ReservoirSampler<Serializable>> samplesPerAttribute = Maps.newHashMap();
        for (Instance<Map<String, Serializable>> instance : trainingData) {
            for (Map.Entry<String,Serializable> attributeKeyValue : instance.getRegressors().entrySet()) {
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
            Iterable<Instance> scrambledTestingSet = Lists.newLinkedList(Iterables.transform(testingSet, new AttributeScrambler(attributeToExclude, samplesForAttribute)));
            double score = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, scrambledTestingSet);
            logger.info("Attribute \""+attributeToExclude+"\" score is "+score);
            scores.add(new AttributeScore(attributeToExclude, score));
        }

        return scores;
    }

    public static class AttributeScrambler implements Function<Instance<Map<String, Serializable>>, Instance<Map<String, Serializable>>> {

        public AttributeScrambler(final String attributeToExclude, ArrayList<Serializable> attributeValueSamples) {
            this.attributeToExclude = attributeToExclude;
            this.attributeValueSamples = attributeValueSamples;
        }

        private final String attributeToExclude;
        private final ArrayList<Serializable> attributeValueSamples;

        public Instance<Map<String, Serializable>> apply(final Instance<Map<String, Serializable>> instance) {
            Map<String, Serializable> randomizedAttributes = new HashMap<>();
            randomizedAttributes.putAll(instance.getRegressors());
            final Serializable randomValue = attributeValueSamples.get(Misc.random.nextInt(attributeValueSamples.size()));
            randomizedAttributes.put(attributeToExclude, randomValue);
            return new InstanceImpl(randomizedAttributes, instance.getLabel());
        }
    }

 }