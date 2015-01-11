package quickml.supervised.neuralNetwork;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quickml.supervised.PredictiveModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ian on 12/23/14.
 */
public final class FeedForwardNeuralNetwork implements PredictiveModel<List<Double>, List<Double>> {
    List<List<Neuron>> layers;
    private int neuronCount;

    public FeedForwardNeuralNetwork(List<Integer> layerSizes) {
        createLayers(layerSizes);

        connectLayers();

    }

    private void createLayers(List<Integer> layerSizes) {
        int neuronIdCounter = 0;
        layers = new ArrayList<>(layerSizes.size());
        for (int numNodes : layerSizes) {
            ArrayList<Neuron> layer = Lists.newArrayListWithCapacity(numNodes);
            for (int x = 0; x < numNodes; x++) {
                layer.add(new Neuron(neuronIdCounter++));
            }
            layers.add(layer);
        }
        neuronCount = neuronIdCounter;
    }

    private void connectLayers() {
        for (int layerNo = 0; layerNo < layers.size() - 1; layerNo++) {
            List<Neuron> lowerLayer = layers.get(layerNo);
            List<Neuron> upperLayer = layers.get(layerNo+1);
            for (Neuron lower : lowerLayer) {
                for (Neuron upper : upperLayer) {
                    Synapse.CONNECT(lower, upper);
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

    public void updateWeightsAndBiases(List<Double> inputs, List<Double> outputs, double learningRate) {
        Preconditions.checkArgument(outputs.size() == getOutputLayer().size());
        double[] activations = computeNeuronActivations(inputs);
        double[] deltas = initializeOutputDeltas(outputs, activations);

        for (int layerIx = layers.size() - 2; layerIx > 0; layerIx--) {
            updateDeltasForLayer(deltas, layers.get(layerIx));
        }

        updateWeightsAndBiasesWithDeltas(deltas);
    }

    private void updateWeightsAndBiasesWithDeltas(double[] deltas) {
        // TODO: Implement updating
    }

    public double[] initializeOutputDeltas(List<Double> outputs, double[] activations) {
        double[] deltas = new double[this.getNeuronCount()];
        int outputNeuronCount = 0;
        for (Neuron neuron : getOutputLayer()) {
            deltas[neuron.getId()] = activations[neuron.getId()] - outputs.get(outputNeuronCount);
            outputNeuronCount++;
        }
        return deltas;
    }

    private void updateDeltasForLayer(double[] deltas, List<Neuron> neurons) {
        for (Neuron neuron : neurons) {
            double runningSumOfError = 0;
            for (Synapse synapse : neuron.getOutputs()) {
                runningSumOfError += synapse.getWeight() * deltas[synapse.b.getId()];
            }
            deltas[neuron.getId()] = runningSumOfError;
        }
    }

    private List<Neuron> getOutputLayer() {
        return layers.get(layers.size() - 1);
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
