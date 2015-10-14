package quickml.supervised.tree.dataProcessing.instanceTranformer;

import quickml.data.AttributesMap;
import quickml.data.instances.InstanceFactory;
import quickml.data.instances.InstanceWithAttributesMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.determineNumericClassLabels;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class LabelToDigitConverter<L extends Serializable, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<L>> implements InstanceTransformer<I, R> {
    final InstanceFactory<R, AttributesMap, L> instanceFactory;
    private Map<Serializable, Double> numericClassLabels;

    public LabelToDigitConverter(InstanceFactory<R, AttributesMap, L> instanceFactory, List<I> trainingData) {
        numericClassLabels = determineNumericClassLabels(trainingData);
        this.instanceFactory = instanceFactory;
    }

    public Map<Serializable, Double> getNumericClassLabels() {
        return numericClassLabels;
    }

    @Override
    public R transformInstance(I instance) {
        return instanceFactory.createInstance(instance.getAttributes(), (L)numericClassLabels.get(instance.getLabel()), instance.getWeight());
    }
}
