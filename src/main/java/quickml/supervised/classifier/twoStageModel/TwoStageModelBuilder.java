package quickml.supervised.classifier.twoStageModel;

import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;

import java.util.List;

/**
 * Created by alexanderhawk on 10/7/14.
 */
public class TwoStageModelBuilder implements PredictiveModelBuilder<AttributesMap,TwoStageModel> {//
    PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder1;
    PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder2;

    public TwoStageModelBuilder(PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder1,
            PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder2)  {
        this.wrappedModelBuilder1 = wrappedModelBuilder1;
        this.wrappedModelBuilder2 = wrappedModelBuilder2;
    }

    @Override
    public TwoStageModel buildPredictiveModel(Iterable<? extends Instance<AttributesMap>> trainingData) {
        List<Instance<AttributesMap>> stage1Data = Lists.newArrayList();
        List<Instance<AttributesMap>> stage2Data = Lists.newArrayList();
        List<Instance<AttributesMap>> validationData = Lists.newArrayList();
        createTrainingAndValidationData(trainingData, stage1Data, stage2Data, validationData);
        Classifier c1 = wrappedModelBuilder1.buildPredictiveModel(stage1Data);
        Classifier c2 = wrappedModelBuilder2.buildPredictiveModel(stage2Data);
        return new TwoStageModel(c1, c2);
    }

    private void createTrainingAndValidationData(Iterable<? extends Instance<AttributesMap>> trainingData,
        List<Instance<AttributesMap>> stage1Data, List<Instance<AttributesMap>> stage2Data,
        List<Instance<AttributesMap>> validationData) {

        for (Instance<AttributesMap> instance : trainingData) {
            if (instance.getLabel().equals("positive-both")) {
                stage1Data.add(new InstanceImpl<>(instance.getAttributes(), 1.0));
                stage2Data.add(new InstanceImpl<>(instance.getAttributes(), 1.0));
                validationData.add(new InstanceImpl<>(instance.getAttributes(), 1.0));
            } else if (instance.getLabel().equals("positive-first")) {
                stage1Data.add(new InstanceImpl<>(instance.getAttributes(), 1.0));
                stage2Data.add(new InstanceImpl<>(instance.getAttributes(), 0.0));
                validationData.add(new InstanceImpl<>(instance.getAttributes(), 0.0));
            } else if (instance.getLabel().equals("negative")) {
                stage1Data.add(new InstanceImpl<>(instance.getAttributes(), 0.0));
                validationData.add(new InstanceImpl<>(instance.getAttributes(), 0.0));
            } else {
                throw new RuntimeException("missing valid label");
            }
        }
    }

}
