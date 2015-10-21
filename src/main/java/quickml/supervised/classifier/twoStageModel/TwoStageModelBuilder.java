package quickml.supervised.classifier.twoStageModel;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/7/14.
 */
public class TwoStageModelBuilder implements PredictiveModelBuilder<TwoStageClassifier, ClassifierInstance> {
    PredictiveModelBuilder< ? extends Classifier, ClassifierInstance> wrappedModelBuilder1;
    PredictiveModelBuilder<? extends Classifier, ClassifierInstance> wrappedModelBuilder2;

    public TwoStageModelBuilder(PredictiveModelBuilder< ? extends Classifier, ClassifierInstance> wrappedModelBuilder1,
                                PredictiveModelBuilder<? extends Classifier, ClassifierInstance> wrappedModelBuilder2) {
        this.wrappedModelBuilder1 = wrappedModelBuilder1;
        this.wrappedModelBuilder2 = wrappedModelBuilder2;
    }

    @Override
    public TwoStageClassifier buildPredictiveModel(Iterable<ClassifierInstance> trainingData) {
        List<ClassifierInstance> stage1Data = Lists.newArrayList();
        List<ClassifierInstance> stage2Data = Lists.newArrayList();
        List<ClassifierInstance> validationData = Lists.newArrayList();

        for (ClassifierInstance instance : trainingData) {
            if (instance.getLabel().equals("positive-both")) {
                stage1Data.add(new ClassifierInstance(instance.getAttributes(), 1.0));
                stage2Data.add(new ClassifierInstance(instance.getAttributes(), 1.0));
                validationData.add(new ClassifierInstance(instance.getAttributes(), 1.0));
            } else if (instance.getLabel().equals("positive-first")) {
                stage1Data.add(new ClassifierInstance(instance.getAttributes(), 1.0));
                stage2Data.add(new ClassifierInstance(instance.getAttributes(), 0.0));
                validationData.add(new ClassifierInstance(instance.getAttributes(), 0.0));
            } else if (instance.getLabel().equals("negative")) {
                stage1Data.add(new ClassifierInstance(instance.getAttributes(), 0.0));
                validationData.add(new ClassifierInstance(instance.getAttributes(), 0.0));
            } else {
                throw new RuntimeException("missing valid label");
            }
        }


        Classifier c1 = wrappedModelBuilder1.buildPredictiveModel(stage1Data);
        Classifier c2 = wrappedModelBuilder2.buildPredictiveModel(stage2Data);
        return new TwoStageClassifier(c1, c2);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        wrappedModelBuilder1.updateBuilderConfig(config);
        wrappedModelBuilder2.updateBuilderConfig(config);
    }
}
