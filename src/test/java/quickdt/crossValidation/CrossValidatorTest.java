package quickdt.crossValidation;

import com.google.common.base.Supplier;
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
    public void testOnlineCrossValidator() {
        Supplier supplier = Mockito.mock(Supplier.class);
        OnlineCrossValLoss onlineCrossValLoss = Mockito.mock(OnlineCrossValLoss.class);

        Mockito.when(supplier.get()).thenReturn(onlineCrossValLoss);

        int folds = 4;
        CrossValidator crossValidator = new CrossValidator(folds, folds, supplier);
        TreeBuilder treeBuilder = new TreeBuilder();
        List<AbstractInstance> instances = getInstances();
        crossValidator.getCrossValidatedLoss(treeBuilder, instances);

        Mockito.verify(onlineCrossValLoss, Mockito.times(instances.size())).addLoss(Mockito.any(AbstractInstance.class), Mockito.any(PredictiveModel.class));
        Mockito.verify(onlineCrossValLoss, Mockito.times(folds)).getTotalLoss();
        //An online cross val loss should not call get average
        Mockito.verify(onlineCrossValLoss, Mockito.never()).getAverageLoss();
    }

    @Test
    public void testCrossValidator() {
        Supplier supplier = Mockito.mock(Supplier.class);
        CrossValLoss crossValLoss = Mockito.mock(CrossValLoss.class);

        Mockito.when(supplier.get()).thenReturn(crossValLoss);

        int folds = 4;
        CrossValidator crossValidator = new CrossValidator(folds, folds, supplier);
        TreeBuilder treeBuilder = new TreeBuilder();
        List<AbstractInstance> instances = getInstances();
        crossValidator.getCrossValidatedLoss(treeBuilder, instances);

        Mockito.verify(crossValLoss, Mockito.times(instances.size())).addLoss(Mockito.any(AbstractInstance.class), Mockito.any(PredictiveModel.class));
        Mockito.verify(crossValLoss, Mockito.times(folds)).getTotalLoss();
        Mockito.verify(crossValLoss, Mockito.times(1)).getAverageLoss();
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
