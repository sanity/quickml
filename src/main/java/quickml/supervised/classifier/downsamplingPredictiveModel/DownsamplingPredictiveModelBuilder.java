package quickml.supervised.classifier.downsamplingPredictiveModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import quickml.supervised.Misc;
import quickml.data.Instance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ian on 4/22/14.
 */
public class DownsamplingPredictiveModelBuilder implements UpdatablePredictiveModelBuilder<Map<String, Serializable>,DownsamplingClassifier> {

    private final double targetMinorityProportion;
    private final PredictiveModelBuilder<Map<String, Serializable>,Classifier> predictiveModelBuilder;

    public DownsamplingPredictiveModelBuilder(PredictiveModelBuilder<Map<String, Serializable>,Classifier> predictiveModelBuilder, double targetMinorityProportion) {
        this.predictiveModelBuilder = predictiveModelBuilder;
        Preconditions.checkArgument(targetMinorityProportion > 0 && targetMinorityProportion < 1, "targetMinorityProportion must be between 0 and 1 (was %s)", targetMinorityProportion);
        this.targetMinorityProportion = targetMinorityProportion;
    }

    @Override
    public DownsamplingClassifier buildPredictiveModel(final Iterable<Instance<Map<String, Serializable>>> trainingData) {
        final Map<Serializable, Double> classificationProportions = getClassificationProportions(trainingData);
        Preconditions.checkArgument(classificationProportions.size() == 2, "trainingData must contain only 2 classifications, but it had %s", classificationProportions.size());
        final Map.Entry<Serializable, Double> majorityEntry = Misc.getEntryWithHighestValue(classificationProportions).get();
        final Map.Entry<Serializable, Double> minorityEntry = Misc.getEntryWithLowestValue(classificationProportions).get();
        Serializable majorityClassification = majorityEntry.getKey();
        final double majorityProportion = majorityEntry.getValue();
        final double naturalMinorityProportion = 1.0 - majorityProportion;
        if (naturalMinorityProportion >= targetMinorityProportion) {
            final Classifier wrappedPredictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);
            return new DownsamplingClassifier(wrappedPredictiveModel, majorityClassification, minorityEntry.getKey(), 0);
        }

        final double dropProbability = 1.0 - ((naturalMinorityProportion - targetMinorityProportion*naturalMinorityProportion) / (targetMinorityProportion - targetMinorityProportion *naturalMinorityProportion));

        Iterable<Instance<Map<String, Serializable>>> downsampledTrainingData = Iterables.filter(trainingData, new RandomDroppingInstanceFilter(majorityClassification, dropProbability));

        final Classifier wrappedPredictiveModel = predictiveModelBuilder.buildPredictiveModel(downsampledTrainingData);

        return new DownsamplingClassifier(wrappedPredictiveModel, majorityClassification, minorityEntry.getKey(), dropProbability);
    }

    @Override
    public PredictiveModelBuilder<Map<String, Serializable>, DownsamplingClassifier> updatable(boolean updatable) {
        predictiveModelBuilder.updatable(updatable);
        return this;
    }

    @Override
    public void setID(Serializable id) {
        predictiveModelBuilder.setID(id);
    }

    private Map<Serializable, Double> getClassificationProportions(final Iterable<Instance<Map<String, Serializable>>> trainingData) {
        Map<Serializable, AtomicLong> classificationCounts = Maps.newHashMap();
        long total = 0;
        for (Instance instance : trainingData) {
            AtomicLong count = classificationCounts.get(instance.getLabel());
            if (count == null) {
                count = new AtomicLong(0);
                classificationCounts.put(instance.getLabel(), count);
            }
            count.incrementAndGet();
            total++;
        }
        Map<Serializable, Double> classificationProportions = Maps.newHashMap();
        for (Map.Entry<Serializable, AtomicLong> classCount : classificationCounts.entrySet()) {
            classificationProportions.put(classCount.getKey(),  classCount.getValue().doubleValue() / (double) total);
        }
        return classificationProportions;
    }

    @Override
    public void updatePredictiveModel(DownsamplingClassifier predictiveModel, Iterable<Instance<Map<String, Serializable>>> newData, boolean splitNodes) {
        if (predictiveModelBuilder instanceof UpdatablePredictiveModelBuilder) {
            Iterable<Instance<Map<String, Serializable>>> downsampledNewData = Iterables.filter(newData, new RandomDroppingInstanceFilter(predictiveModel.getMajorityClassification(), predictiveModel.getDropProbability()));
            ((UpdatablePredictiveModelBuilder)predictiveModelBuilder).updatePredictiveModel(predictiveModel.wrappedClassifier, downsampledNewData, splitNodes);
        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

    @Override
    public void stripData(DownsamplingClassifier predictiveModel) {
        if (predictiveModelBuilder instanceof UpdatablePredictiveModelBuilder) {
            ((UpdatablePredictiveModelBuilder) predictiveModelBuilder).stripData(predictiveModel.wrappedClassifier);
        } else {
            throw new RuntimeException("Cannot strip data without UpdatablePredictiveModelBuilder");
        }
    }
}
