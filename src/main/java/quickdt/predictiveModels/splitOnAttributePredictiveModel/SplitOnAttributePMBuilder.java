package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributePMBuilder implements PredictiveModelBuilder<SplitOnAttributePM> {
    private static final  Logger logger =  LoggerFactory.getLogger(SplitOnAttributePMBuilder.class);

    public static final Double NO_VALUE_PLACEHOLDER = Double.MIN_VALUE;

    private final String attributeKey;
    private final PredictiveModelBuilder<?> wrappedBuilder;

    public SplitOnAttributePMBuilder(String attributeKey, PredictiveModelBuilder<?> wrappedBuilder) {
        this.attributeKey = attributeKey;
        this.wrappedBuilder = wrappedBuilder;
    }

    @Override
    public SplitOnAttributePM buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData = Maps.newHashMap();
        for (AbstractInstance instance : trainingData) {
            Serializable value = instance.getAttributes().get(attributeKey);
            if (value == null) value = NO_VALUE_PLACEHOLDER;
            ArrayList<AbstractInstance> splitData = splitTrainingData.get(value);
            if (splitData == null) {
                splitData = Lists.newArrayList();
                splitTrainingData.put(value, splitData);
            }
            splitData.add(instance);
        }

        Map<Serializable, PredictiveModel> splitModels = Maps.newHashMap();
        for (Map.Entry<Serializable, ArrayList<AbstractInstance>> trainingDataEntry : splitTrainingData.entrySet()) {
            logger.info("Building predictive model for "+attributeKey+"="+trainingDataEntry.getKey());
            splitModels.put(trainingDataEntry.getKey(), wrappedBuilder.buildPredictiveModel(trainingDataEntry.getValue()));
        }

        logger.info("Building default predictive model");
        final PredictiveModel defaultPM = wrappedBuilder.buildPredictiveModel(trainingData);
        return new SplitOnAttributePM(attributeKey, splitModels, defaultPM);
    }

    @Override
    public PredictiveModelBuilder<SplitOnAttributePM> updatable(final boolean updatable) {
        throw new UnsupportedOperationException();
    }
}
