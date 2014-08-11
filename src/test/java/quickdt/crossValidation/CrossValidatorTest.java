package quickdt.crossValidation;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import quickdt.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.Instance;
import quickdt.data.InstanceImpl;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.decisionTree.TreeBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 5/6/2014.
 */
public class CrossValidatorTest {

    @Test
    public void testCrossValidator() {
        CrossValLossFunction<PredictiveModel<Object,Object>> crossValLossFunction = Mockito.mock(CrossValLossFunction.class);

        int folds = 4;
        CrossValidator crossValidator = new StationaryCrossValidator(folds, folds, crossValLossFunction);
        TreeBuilder treeBuilder = new TreeBuilder();
        List<Instance> instances = getInstances();
        crossValidator.getCrossValidatedLoss(treeBuilder, instances);

        Mockito.verify(crossValLossFunction, Mockito.times(folds)).getLoss(Mockito.<List<LabelPredictionWeight<PredictiveModel<Object,Object>>>>any());
    }

    private List<Instance> getInstances() {
        final List<Instance> instances = Lists.newLinkedList();
        for(int i = 0; i < 5; i++) {
            Map<String, Serializable> attributes = new HashMap<>();

            Instance instance = Mockito.mock(InstanceImpl.class);
            Mockito.when(instance.getWeight()).thenReturn(1.0);
            Mockito.when(instance.getLabel()).thenReturn("class");
            Mockito.when(instance.getRegressors()).thenReturn(attributes);
            instances.add(instance);
        }
        return instances;
    }
}
