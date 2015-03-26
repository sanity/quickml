package quickml.supervised.neuralNetwork;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quickml.supervised.PredictiveModel;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ian on 12/23/14.
 */
public final class FeedForwardNeuralNetwork implements PredictiveModel<List<Double>, List<Double>> {
    List<List<Neuron>> layers;
    private int neuronCount;

    public FeedForwardNeuralNetwork(List<Integer> layerSizes) {
        Preconditions.checkArgument(layerSizes.size() > 1);
        createLayers(layerSizes);

        connectLayers();

    }

    private void createLayers(List<Integer> layerSizes) {
        Random random = new Random();
        int neuronIdCounter = 0;
        layers = new ArrayList<>(layerSizes.size());
        for (int numNodes : layerSizes) {
            ArrayList<Neuron> layer = Lists.newArrayListWithCapacity(numNodes);
            for (int x = 0; x < numNodes; x++) {
                boolean isInputLayer = layers.isEmpty();
                Neuron neuron = new Neuron(neuronIdCounter++, isInputLayer);
                neuron.setBias(random.nextDouble()*2.0-1.0);
                layer.add(neuron);
            }
            layers.add(layer);
        }
        neuronCount = neuronIdCounter;
    }

    private void connectLayers() {
        Random random = new Random();
        for (int layerNo = 0; layerNo < layers.size() - 1; layerNo++) {
            List<Neuron> lowerLayer = layers.get(layerNo);
            List<Neuron> upperLayer = layers.get(layerNo+1);
            for (Neuron lower : lowerLayer) {
                for (Neuron upper : upperLayer) {
                    Synapse.CONNECT(lower, upper).updateWeight(random.nextDouble()*0.1+0.01);
                }
            }
        }
    }

    @java.lang.Override
    public List<Double> predict(List<Double> inputNeuronActivations) {
        double[] neuronActivations = computeNeuronActivations(inputNeuronActivations);
        return extractOutputNeuronActivations(neuronActivations);
    }

    public double[] computeNeuronActivations(List<Double> inputNeuronActivations) {
        double[] neuronActivations = convertInputActivationsToActivationMap(inputNeuronActivations);
        for (List<Neuron> layer : layers) {
            if (layer == getInputLayer()) {
                continue;
            }
            for (Neuron neuron : layer) {
                neuron.computeAndStoreOutputActivation(neuronActivations);
            }
        }
        return neuronActivations;
    }

    private List<Double> extractOutputNeuronActivations(double[] neuronActivations) {
        List<Double> outputActivations = Lists.newArrayListWithCapacity(getOutputLayer().size());
        for (Neuron neuron : getOutputLayer()) {
            outputActivations.add(neuronActivations[neuron.getId()]);
        }
        return outputActivations;
    }

    private double[] convertInputActivationsToActivationMap(List<Double> inputNeuronActivations) {
        int inputLayerSize = getInputLayer().size();
        int inputNeuronActivationsSize = inputNeuronActivations.size();
        Preconditions.checkArgument(inputNeuronActivationsSize == inputLayerSize,
                String.format("%d input activations, but %d input neurons", inputNeuronActivationsSize, inputLayerSize));
        double[] neuronActivations = new double[neuronCount];
        for (int neuronIx = 0; neuronIx < inputLayerSize; neuronIx++) {
            Neuron inputNeuron = getInputLayer().get(neuronIx);
            neuronActivations[inputNeuron.getId()] = inputNeuronActivations.get(neuronIx);
        }
        return neuronActivations;
    }

    private List<Neuron> getInputLayer() {
        return layers.get(0);
    }

    private List<Neuron> getOutputLayer() {
        return layers.get(layers.size() - 1);
    }

    public void updateWeightsAndBiases(List<Double> inputs, List<Double> outputs, double learningRate) {
        Preconditions.checkArgument(outputs.size() == getOutputLayer().size());
        double[] activations = computeNeuronActivations(inputs);
        double[] deltas = initializeOutputDeltas(outputs, activations);

        for (int layerIx = layers.size() - 2; layerIx > 0; layerIx--) {
            updateDeltasForLayer(activations, deltas, layers.get(layerIx));
        }

        updateWeightsAndBiasesWithDeltas(learningRate, deltas, activations);
    }

    public double[] initializeOutputDeltas(List<Double> outputs, double[] activations) {
        double[] deltas = new double[this.getNeuronCount()];
        int outputNeuronCount = 0;
        for (Neuron neuron : getOutputLayer()) {
            final double desiredOutput = outputs.get(outputNeuronCount);
            final double actualOutput = activations[neuron.getId()];
            deltas[neuron.getId()] =  desiredOutput - actualOutput;
            outputNeuronCount++;
        }
        return deltas;
    }

    private void updateDeltasForLayer(double activations[], double[] deltas, List<Neuron> neurons) {
        for (Neuron neuron : neurons) {
            double runningSumOfDelta = 0;
            for (Synapse synapse : neuron.getOutputs()) {
                final double delta = deltas[synapse.b.getId()];
                final double weight = synapse.getWeight();
                final double activation = activations[neuron.getId()];
                runningSumOfDelta += weight * delta * activation * (1.0 - activation);
            }
            deltas[neuron.getId()] = runningSumOfDelta;
        }
    }

    private void updateWeightsAndBiasesWithDeltas(double learningRate, double[] deltas, double[] activations) {
        for (List<Neuron> layer : layers) {
            for (Neuron neuron : layer) {
                neuron.updateWeightsAndBias(learningRate, activations, deltas);
            }
        }
    }

    public int getNeuronCount() {
        return neuronCount;
    }

    @java.lang.Override
    public List<Double> predictWithoutAttributes(List<Double> attributes, Set<String> attributesToIgnore) {
        // TODO: Once this method has been removed from PredictiveModel this implementation should be removed
        throw new UnsupportedOperationException();
    }

    @java.lang.Override
    public void dump(Appendable appendable) {

    }
}
