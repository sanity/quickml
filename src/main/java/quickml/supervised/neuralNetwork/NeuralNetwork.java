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
public final class NeuralNetwork implements PredictiveModel<List<Double>, List<Double>> {
    List<List<Neuron>> layers;
    private int neuronCount;

    public NeuralNetwork(List<Integer> layerSizes) {
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
        for (int layerNo = 0; layerNo < layers.size() - 2; layerNo++) {
            List<Neuron> lowerLayer = layers.get(layerNo);
            List<Neuron> upperLayer = layers.get(layerNo+1);
            for (Neuron lower : lowerLayer) {
                for (Neuron upper : upperLayer) {
                    Synapse.CONNECT(lower, upper);
                }
            }
        }
    }

    public int getNeuronCount() {
        return neuronCount;
    }

    @java.lang.Override
    public List<Double> predict(List<Double> inputNeuronActivations) {
        double[] neuronActivations = convertInputActivationsToActivationMap(inputNeuronActivations);
        for (List<Neuron> layer : layers) {
            for (Neuron neuron : layer) {
                neuron.computeAndStoreOutputActivation(neuronActivations);
            }
        }
        return extractOutputNeuronActivations(neuronActivations);
    }

    private double[] convertInputActivationsToActivationMap(List<Double> inputNeuronActivations) {
        List<Neuron> inputNeurons = layers.get(0);
        int inputLayerSize = inputNeurons.size();
        int inputNeuronActivationsSize = inputNeuronActivations.size();
        Preconditions.checkArgument(inputNeuronActivationsSize == inputLayerSize,
                String.format("%d input activations, but %d input neurons", inputNeuronActivationsSize, inputLayerSize));
        double[] neuronActivations = new double[neuronCount];
        for (int neuronIx = 0; neuronIx < inputLayerSize; neuronIx++) {
            Neuron inputNeuron = inputNeurons.get(neuronIx);
            neuronActivations[inputNeuron.getId()] = inputNeuronActivations.get(neuronIx);
        }
        return neuronActivations;
    }

    private List<Double> extractOutputNeuronActivations(double[] neuronActivations) {
        List<Neuron> outputLayer = layers.get(layers.size() - 1);
        List<Double> outputActivations = Lists.newArrayListWithCapacity(outputLayer.size());
        for (Neuron neuron : outputLayer) {
            outputActivations.add(neuronActivations[neuron.getId()]);
        }
        return outputActivations;
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
