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
        if (isInputNeuron) {
            throw new IllegalStateException("Input neurons don't compute output activation");
        }
        double inputActivation = computeInputActivation(neuronActivations);
        return activationFunction.apply(inputActivation + bias);
    }

    protected double computeInputActivation(double[] neuronActivations) {
        if (isInputNeuron) {
            throw new IllegalStateException("Input neurons don't compute input activation");
        }
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
        if (isInputNeuron) {
            throw new IllegalStateException("Input neurons don't have input synapses");
        }
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

    public void updateWeightsAndBias(double learningRate, double activations[], double[] deltas) {
        Preconditions.checkState(activationFunction == Sigmoid.SINGLETON, "Only sigmoid activation function is currently supported for training");
        double activation = activations[id];
        if (!isInputNeuron) {
            bias = bias + learningRate * 1.0 * deltas[this.getId()];
        }
        for (Synapse synapse : outputs) {
            double outputDelta = deltas[synapse.b.getId()];
            double correction = learningRate * 1.0 * activation * outputDelta;
            synapse.updateWeight(synapse.getWeight() + correction);
        }
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "id=" + id +
                ", bias=" + bias +
                ", isInputNeuron=" + isInputNeuron +
                '}';
    }
}
