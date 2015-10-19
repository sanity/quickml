package quickml.supervised.dataProcessing.instanceTranformer;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.SparseClassifierInstanceFactory;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils.populateNameToIndexMap;

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

    public HashMap<String, Integer> getNameToIndexMap() {
        return nameToIndexMap;
    }

    public SparseClassifierInstanceFactory getInstanceFactory() {
        return instanceFactory;
    }

    public static <L extends Serializable, I extends ClassifierInstance> List<SparseClassifierInstance> transformAllInstances(List<I> instances) {
        ClassifierInstance2SparseClassifierInstance transformer = new ClassifierInstance2SparseClassifierInstance(instances);
        SparseClassifierInstanceFactory instanceFactory = transformer.getInstanceFactory();
        List<SparseClassifierInstance> returnInstances = Lists.<SparseClassifierInstance>newArrayList();
        for (I instance : instances) {
            returnInstances.add(instanceFactory.createInstance(instance.getAttributes(), instance.getLabel(), instance.getWeight()));
        }
        return returnInstances;
    }

    @Override
    public SparseClassifierInstance transformInstance(I instance) {
        return instanceFactory.createInstance(instance.getAttributes(), instance.getLabel(), instance.getWeight());
    }
}
