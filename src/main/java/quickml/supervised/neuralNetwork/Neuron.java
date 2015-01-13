package quickml.supervised.neuralNetwork;

import com.google.common.base.Preconditions;
import quickml.supervised.neuralNetwork.activationFunctions.Sigmoid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ian on 12/23/14.
 */
/* FIXME: Input neurons really don't do much, they're placeholders and most of the logic
 *       in this class isn't used for input neurons.  This doesn't really hurt but it
 *       is an annoying inelegance.
 */
public final class Neuron {
    private final ActivationFunction activationFunction;
    private final int id;
    private final boolean isInputNeuron;

    List<Synapse> inputs = new ArrayList<>(), outputs = new ArrayList<>();
    private double bias = 0;

    public Neuron(int id, boolean isInputNeuron) {
        this(id, Sigmoid.SINGLETON, isInputNeuron);
    }

    public Neuron(int id, ActivationFunction activationFunction, boolean isInputNeuron) {
        this.id = id;
        this.activationFunction = activationFunction;
        this.isInputNeuron = isInputNeuron;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    protected void computeAndStoreOutputActivation(double[] neuronActivations) {
        neuronActivations[id] = computeOutputActivation(neuronActivations);
    }

    public double computeOutputActivation(double[] neuronActivations) {
        return activationFunction.apply(computeInputActivation(neuronActivations) + bias);
    }

    protected double computeInputActivation(double[] neuronActivations) {
        double activation = 0.0;
        for (Synapse synapse : inputs) {
            Double aActivation = neuronActivations[synapse.a.getId()];
            if (aActivation == null) {
                throw new IllegalArgumentException("No activation for input node "+ synapse.a.getId());
            } else {
                activation += synapse.getWeight() * aActivation;
            }
        }
        return activation;
    }

    public int getId() {
        return id;
    }

    public List<Synapse> getInputs() {
        return inputs;
    }

    public List<Synapse> getOutputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Neuron neuron = (Neuron) o;

        if (id != neuron.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void updateWeightsAndBias(double learningRate, double activation, double delta) {
        Preconditions.checkState(activationFunction == Sigmoid.SINGLETON, "Only sigmoid activation function is currently supported for training");
        if (!isInputNeuron) {
            bias = bias + learningRate * 1.0 * delta;
        }
        for (Synapse synapse : outputs) {
            double outputOfA = activationFunction.apply(activation);
            synapse.updateWeight(synapse.getWeight() + learningRate * 1.0 * outputOfA * delta);
        }
    }
}
