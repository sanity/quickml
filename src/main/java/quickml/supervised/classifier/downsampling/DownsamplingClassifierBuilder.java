package quickml.supervised.classifier.downsampling;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.AttributesMapPredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by ian on 4/22/14.
 */

public class DownsamplingClassifierBuilder<T extends ClassifierInstance> implements AttributesMapPredictiveModelBuilder<Classifier, T> {

    public static final String MINORITY_INSTANCE_PROPORTION = "minorityInstanceProportion";


    private static final Logger logger = LoggerFactory.getLogger(DownsamplingClassifierBuilder.class);
    private double targetMinorityProportion;
    private final AttributesMapPredictiveModelBuilder<? extends Classifier, T> predictiveModelBuilder;

    public DownsamplingClassifierBuilder(AttributesMapPredictiveModelBuilder<? extends Classifier, T> predictiveModelBuilder, double targetMinorityProportion) {
        checkArgument(targetMinorityProportion > 0 && targetMinorityProportion < 1, "targetMinorityProportion must be between 0 and 1 (was %s)", targetMinorityProportion);
        this.predictiveModelBuilder = predictiveModelBuilder;
        this.targetMinorityProportion = targetMinorityProportion;
    }

    @Override
    public DownsamplingClassifier buildPredictiveModel(Iterable<T> trainingData) {
        final Map<Object, Double> classificationProportions = getClassificationProportions(trainingData);
        if (classificationProportions.size() != 2) {
            printSampleInstancesForInspection(trainingData);
        }
        checkArgument(classificationProportions.size() == 2, "trainingData must contain only 2 classifications, but it had %s. mapOfClassificationsToOutcomes: %s", classificationProportions.size(), classificationProportions.get(1.0), classificationProportions.toString());
        final Map.Entry<Object, Double> majorityEntry = MapUtils.getEntryWithHighestValue(classificationProportions).get();
        final Map.Entry<Object, Double> minorityEntry = MapUtils.getEntryWithLowestValue(classificationProportions).get();
        Object majorityClassification = majorityEntry.getKey();
        final double majorityProportion = majorityEntry.getValue();
        final double naturalMinorityProportion = 1.0 - majorityProportion;
        if (naturalMinorityProportion >= targetMinorityProportion) {
            final Classifier wrappedPredictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);
            return new DownsamplingClassifier(wrappedPredictiveModel, majorityClassification, minorityEntry.getKey(), 0);
        }

        final double dropProbability = (naturalMinorityProportion > targetMinorityProportion) ? 0 : 1.0 - ((naturalMinorityProportion - targetMinorityProportion * naturalMinorityProportion) / (targetMinorityProportion - targetMinorityProportion * naturalMinorityProportion));

        Iterable<T> downsampledTrainingData = Iterables.filter(trainingData, new RandomDroppingInstanceFilter(majorityClassification, dropProbability));

        final Classifier wrappedPredictiveModel = predictiveModelBuilder.buildPredictiveModel(downsampledTrainingData);

        return new DownsamplingClassifier(wrappedPredictiveModel, majorityClassification, minorityEntry.getKey(), dropProbability);
    }


    @Override
    public void updateBuilderConfig(Map<String, Object> cfg) {
        predictiveModelBuilder.updateBuilderConfig(cfg);
        if (cfg.containsKey(MINORITY_INSTANCE_PROPORTION))
            targetMinorityProportion((Double) cfg.get(MINORITY_INSTANCE_PROPORTION));
    }

    public DownsamplingClassifierBuilder targetMinorityProportion(double targetMinorityProportion) {
        this.targetMinorityProportion = targetMinorityProportion;
        return this;
    }

    private void printSampleInstancesForInspection(Iterable<? extends InstanceWithAttributesMap> trainingData) {
        logger.info("length of training data" + Iterables.size(trainingData));
        int counter = 0;
        for (InstanceWithAttributesMap instance : trainingData) {
            if (counter++ % 100 == 0) {
                if (instance.getLabel().equals(Double.valueOf(1.0))) {
                    logger.info("instance " + counter);
                    logger.info(instance.getAttributes().toString());
                    logger.info("label:" + instance.getLabel().toString());
                    logger.info("weight:" + instance.getWeight());
                }
            }
            if (counter > 1000) break;
        }
    }

    private Map<Object, Double> getClassificationProportions(final Iterable<? extends InstanceWithAttributesMap> trainingData) {
        Map<Object, AtomicLong> classificationCounts = Maps.newHashMap();
        long total = 0;
        for (InstanceWithAttributesMap instance : trainingData) {
            AtomicLong count = classificationCounts.get(instance.getLabel());
            if (count == null) {
                count = new AtomicLong(0);
                classificationCounts.put(instance.getLabel(), count);
            }
            count.getAndIncrement();
            total++;
        }
        Map<Object, Double> classificationProportions = Maps.newHashMap();
        for (Map.Entry<Object, AtomicLong> classCount : classificationCounts.entrySet()) {
            classificationProportions.put(classCount.getKey(), classCount.getValue().doubleValue() / (double) total);
        }
        return classificationProportions;
    }
}
