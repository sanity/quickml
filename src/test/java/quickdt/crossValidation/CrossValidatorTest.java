package quickdt.crossValidation;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.decisionTree.TreeBuilder;

import java.util.List;

/**
 * Created by Chris on 5/6/2014.
 */
public class CrossValidatorTest {

    @Test
    public void testCrossValidator() {
        CrossValLoss crossValLoss = Mockito.mock(CrossValLoss.class);

        int folds = 4;
        CrossValidator crossValidator = new StationaryCrossValidator(folds, folds, crossValLoss);
        TreeBuilder treeBuilder = new TreeBuilder();
        List<AbstractInstance> instances = getInstances();
        crossValidator.getCrossValidatedLoss(treeBuilder, instances);

        Mockito.verify(crossValLoss, Mockito.times(folds)).getLoss(Mockito.<List<AbstractInstance>>any(), Mockito.any(PredictiveModel.class));
    }

    private List<AbstractInstance> getInstances() {
        final List<AbstractInstance> instances = Lists.newLinkedList();
        for(int i = 0; i < 5; i++) {
            Attributes attributes = new HashMapAttributes();

            AbstractInstance instance = Mockito.mock(Instance.class);
            Mockito.when(instance.getWeight()).thenReturn(1.0);
            Mockito.when(instance.getClassification()).thenReturn("class");
            Mockito.when(instance.getAttributes()).thenReturn(attributes);
            instances.add(instance);
        }
        return instances;
    }
}
