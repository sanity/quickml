package quickml.supervised.tree.dataProcessing.instanceTranformer;

import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.InstanceFactory;
import quickml.data.instances.SparseClassifierInstanceFactory;
import quickml.supervised.classifier.logRegression.SparseClassifierInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.populateNameToIndexMap;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class ClassifierInstance2SparseClassifierInstance<L extends Serializable, I extends ClassifierInstance> implements InstanceTransformer<I, SparseClassifierInstance> {

    private SparseClassifierInstanceFactory instanceFactory;
    private  HashMap<String, Integer> nameToIndexMap;

    public ClassifierInstance2SparseClassifierInstance(List<I> trainingData) {
        this.nameToIndexMap = populateNameToIndexMap(trainingData);
        this.instanceFactory = new SparseClassifierInstanceFactory(nameToIndexMap);
    }

    @Override
    public SparseClassifierInstance transformInstance(I instance) {
        return instanceFactory.createInstance(instance.getAttributes(), instance.getLabel(), instance.getWeight());
    }
}
