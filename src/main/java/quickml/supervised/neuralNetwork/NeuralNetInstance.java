package quickml.supervised.neuralNetwork;

import quickml.data.Instance;

import java.util.List;

/**
 * Created by ian on 2/2/15.
 */
public class NeuralNetInstance implements Instance<List<Double>, List<Double>> {
    private final List<Double> inputs;
    private final List<Double> outputs;

    public NeuralNetInstance(List<Double> inputs, List<Double> outputs) {

        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public List<Double> getAttributes() {
        return inputs;
    }

    @Override
    public List<Double> getLabel() {
        return outputs;
    }

    @Override
    public double getWeight() {
        return 1.0;
    }
}
