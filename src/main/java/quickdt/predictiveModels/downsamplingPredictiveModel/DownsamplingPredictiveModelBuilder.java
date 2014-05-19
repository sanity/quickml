package quickdt.predictiveModels.downsamplingPredictiveModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Misc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ian on 4/22/14.
 */
public class    DownsamplingPredictiveModelBuilder implements PredictiveModelBuilder<DownsamplingPredictiveModel> {

    private static final  Logger logger =  LoggerFactory.getLogger(DownsamplingPredictiveModelBuilder.class);

    private final double targetMinorityProportion;
    private final PredictiveModelBuilder<?> predictiveModelBuilder;

    public DownsamplingPredictiveModelBuilder(PredictiveModelBuilder<?> predictiveModelBuilder, double targetMinorityProportion) {
        this.predictiveModelBuilder = predictiveModelBuilder;
        Preconditions.checkArgument(targetMinorityProportion > 0 && targetMinorityProportion < 1, "targetMinorityProportion must be between 0 and 1 (was %s)", targetMinorityProportion);
        this.targetMinorityProportion = targetMinorityProportion;
    }

    @Override
    public DownsamplingPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        final Map<Serializable, Double> classificationProportions = getClassificationProportions(trainingData);
        Preconditions.checkArgument(classificationProportions.size() == 2, "trainingData must contain only 2 classifications, but it had %s", classificationProportions.size());
        final Map.Entry<Serializable, Double> majorityEntry = Misc.getEntryWithHighestValue(classificationProportions).get();
        Serializable majorityClassification = majorityEntry.getKey();
        final double majorityProportion = majorityEntry.getValue();
        final double naturalMinorityProportion = 1.0 - majorityProportion;
        if (naturalMinorityProportion >= targetMinorityProportion) {
            final PredictiveModel wrappedPredictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);
            return new DownsamplingPredictiveModel(wrappedPredictiveModel, majorityClassification, 0);
        }

        final double dropProbability = 1.0 - ((naturalMinorityProportion - targetMinorityProportion*naturalMinorityProportion) / (targetMinorityProportion - targetMinorityProportion *naturalMinorityProportion));

        Iterable<? extends AbstractInstance> downsampledTrainingData = Iterables.filter(trainingData, new RandomDroppingInstanceFilter(majorityClassification, dropProbability));

        final PredictiveModel wrappedPredictiveModel = predictiveModelBuilder.buildPredictiveModel(downsampledTrainingData);

        return new DownsamplingPredictiveModel(wrappedPredictiveModel, majorityClassification, dropProbability);
    }

    private Map<Serializable, Double> getClassificationProportions(final Iterable<? extends AbstractInstance> trainingData) {
        Map<Serializable, AtomicLong> classificationCounts = Maps.newHashMap();
        long total = 0;
        for (AbstractInstance instance : trainingData) {
            AtomicLong count = classificationCounts.get(instance.getClassification());
            if (count == null) {
                count = new AtomicLong(0);
                classificationCounts.put(instance.getClassification(), count);
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
}
