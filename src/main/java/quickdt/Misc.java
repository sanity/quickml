package quickdt;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Misc {
	public static final Random random = new Random();

    public static final <T extends Serializable, P extends PredictiveModel<T>> List<LabelPredictionWeight<T>> createLabelPredictionPairs(P predictiveModel, List<AbstractInstance<T>> instances){
        List<LabelPredictionWeight<T>> instancePredictionPairs = Lists.newArrayList();
        for (AbstractInstance<T> instance : instances ) {
            instancePredictionPairs.add(new LabelPredictionWeight<T>(instance.getLabel(), predictiveModel.predictionForLabeledInstace(instance), instance.getWeight()));
        }
        return instancePredictionPairs;
    }

    public static final <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> getEntryWithLowestValue(Map<K, V> map) {
        Optional<Map.Entry<K, V>> entryWithLowestValue = Optional.absent();
        for (Map.Entry<K, V> kvEntry : map.entrySet()) {
            if (!entryWithLowestValue.isPresent() || entryWithLowestValue.get().getValue().compareTo(kvEntry.getValue()) >= 0){
                entryWithLowestValue = Optional.of(kvEntry);
            }
        }
        return entryWithLowestValue;
    }


    public static final <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> getEntryWithHighestValue(Map<K, V> map) {
        Optional<Map.Entry<K, V>> entryWithHighestValue = Optional.absent();
        for (Map.Entry<K, V> kvEntry : map.entrySet()) {
            if (!entryWithHighestValue.isPresent() || entryWithHighestValue.get().getValue().compareTo(kvEntry.getValue()) <= 0){
                entryWithHighestValue = Optional.of(kvEntry);
            }
        }
        return entryWithHighestValue;
    }
}
