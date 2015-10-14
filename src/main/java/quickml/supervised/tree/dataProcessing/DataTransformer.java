package quickml.supervised.tree.dataProcessing;

import com.google.common.collect.Lists;
import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class DataTransformer<I extends Instance, R extends Instance> {

    List<InstanceTransformer<I, I>> input2InputTypeTransformers = Lists.newArrayList();
    InstanceTransformer<I, R> input2ReturnTypeTransformer;
    List<InstanceTransformer<R, R>> return2ReturnTypeTransformer = Lists.newArrayList();

    public DataTransformer(List<InstanceTransformer<I, I>> input2InputTypeTransformers) {
        this.input2InputTypeTransformers = input2InputTypeTransformers;
    }

    public DataTransformer(List<InstanceTransformer<I, I>> input2InputTypeTransformers, InstanceTransformer<I, R> input2ReturnTypeTransformer) {
        this(input2InputTypeTransformers);
        this.input2ReturnTypeTransformer = input2ReturnTypeTransformer;
    }

    public DataTransformer(List<InstanceTransformer<I, I>> input2InputTypeTransformers, InstanceTransformer<I, R> input2ReturnTypeTransformer, List<InstanceTransformer<R, R>> return2ReturnTypeTransformer) {
        this(input2InputTypeTransformers, input2ReturnTypeTransformer);
        this.return2ReturnTypeTransformer = return2ReturnTypeTransformer;
    }

    public List<R> transformInstances(List<I> inputInstances) {
        I transformedInput;
        R transformedOutput;
        List<R> outputInstances = Lists.newArrayList();

        for (I instance : inputInstances) {
            transformedInput = doInput2InputTransformation(instance);
            if (input2ReturnTypeTransformer == null) {
                transformedOutput = (R) instance;
            } else {
                transformedOutput = doInput2ReturnTypeTransformation(transformedInput);
                transformedOutput = doOutput2OutputTransformation(transformedOutput);
            }
            outputInstances.add(transformedOutput);
        }
        return outputInstances;
    }

    private R doOutput2OutputTransformation(R transformedOutput) {
        for (InstanceTransformer<R, R> output2Output : return2ReturnTypeTransformer) {
            transformedOutput = output2Output.transformInstance(transformedOutput);
        }
        return transformedOutput;
    }

    private R doInput2ReturnTypeTransformation(I transformedInput) {
        return input2ReturnTypeTransformer.transformInstance(transformedInput);
    }

    private I doInput2InputTransformation(I instance) {
        I transformedInput = instance;
        for (InstanceTransformer<I, I> input2Input : input2InputTypeTransformers) {
            transformedInput = input2Input.transformInstance(transformedInput);
        }
        return transformedInput;
    }
}

