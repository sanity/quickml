package quickdt.predictiveModels;

import com.google.common.collect.Lists;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;

import java.util.List;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public abstract class AbstractPredictiveModel<Pr extends Prediction> implements PredictiveModel<Pr> {

    @Override
    public List<LabelPredictionWeight<Pr>> createLabelPredictionWeights(List<AbstractInstance> instances){
        List<LabelPredictionWeight<Pr>> labelPredictionWeights = Lists.newArrayList();
        for (AbstractInstance instance : instances ) {
            labelPredictionWeights.add(new LabelPredictionWeight<>(instance.getLabel(), predict(instance.getAttributes()), instance.getWeight()));
        }
        return labelPredictionWeights;
    }

}
