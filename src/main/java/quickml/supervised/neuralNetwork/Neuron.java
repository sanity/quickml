package quickml.supervised.neuralNetwork;

import quickml.supervised.neuralNetwork.activationFunctions.Sigmoid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ian on 12/23/14.
 */
public final class Neuron {
    private final ActivationFunction activationFunction;
    private final int id;
    List<Synapse> inputs = new ArrayList<>(), outputs = new ArrayList<>();

    public Neuron(int id) {
        this(id, Sigmoid.SINGLETON);
    }

    public Neuron(int id, ActivationFunction activationFunction) {
        this.id = id;
        this.activationFunction = activationFunction;
    }

    protected void computeAndStoreOutputActivation(double[] neuronActivations) {
        neuronActivations[id] = computeOutputActivation(neuronActivations);
    }

    public double computeOutputActivation(double[] neuronActivations) {
        return activationFunction.apply(computeInputActivation(neuronActivations));
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
}
