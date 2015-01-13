package quickml.supervised.neuralNetwork;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class FeedForwardNeuralNetworkTest {
    @Test
    public void testNetCreation() {
        FeedForwardNeuralNetwork feedForwardNeuralNetwork = new FeedForwardNeuralNetwork(Lists.newArrayList(1, 1));
        Assert.assertEquals(feedForwardNeuralNetwork.layers.size(), 2);
        Assert.assertEquals(feedForwardNeuralNetwork.getNeuronCount(), 2);
        List<Neuron> firstLayer = feedForwardNeuralNetwork.layers.get(0);
        Assert.assertEquals(firstLayer.size(), 1);
        Neuron firstNeuron = firstLayer.get(0);
        verifyFirstNeuron(firstNeuron);
        List<Neuron> secondLayer = feedForwardNeuralNetwork.layers.get(1);
        Assert.assertEquals(secondLayer.size(), 1);
        Neuron secondNeuron = secondLayer.get(0);
        Assert.assertEquals(secondNeuron.getInputs().size(), 1);
        Assert.assertEquals(secondNeuron.getInputs().get(0), firstNeuron.getOutputs().get(0));
        Assert.assertTrue(secondNeuron.getOutputs().isEmpty());
        verifySynapse(firstNeuron, secondNeuron);
    }

    private void verifyFirstNeuron(Neuron firstNeuron) {
        Assert.assertEquals(firstNeuron.getId(), 0);
        Assert.assertTrue(firstNeuron.getInputs().isEmpty());
        Assert.assertEquals(firstNeuron.getOutputs().size(), 1);
    }

    private void verifySynapse(Neuron firstNeuron, Neuron secondNeuron) {
        Synapse firstSynapse = firstNeuron.getOutputs().get(0);
        Assert.assertEquals(firstSynapse.a, firstNeuron);
        Assert.assertEquals(firstSynapse.b, secondNeuron);
    }

    @Test
    public void simpleTest() {
        FeedForwardNeuralNetwork feedForwardNeuralNetwork = new FeedForwardNeuralNetwork(Lists.newArrayList(2, 1));
        List<Neuron> inputLayer = feedForwardNeuralNetwork.layers.get(0);
        Neuron inputNeuron1 = inputLayer.get(0);
        inputNeuron1.getOutputs().get(0).updateWeight(0.5);
        Neuron inputNeuron2 = inputLayer.get(1);
        inputNeuron2.getOutputs().get(0).updateWeight(0.5);
    }

    @Test
    public void xorTest() {
        FeedForwardNeuralNetwork feedForwardNeuralNetwork = new FeedForwardNeuralNetwork(Lists.newArrayList(2, 2, 1));
        List<XorTrainingPair> trainingData = Lists.newArrayList();
        trainingData.add(new XorTrainingPair(0, 0, 0));
        trainingData.add(new XorTrainingPair(1, 0, 1));
        trainingData.add(new XorTrainingPair(0, 1, 1));
        trainingData.add(new XorTrainingPair(1, 1, 0));

        for (int x=0; x<1000; x++) {
            for (XorTrainingPair xorTrainingPair : trainingData) {
                feedForwardNeuralNetwork.updateWeightsAndBiases(xorTrainingPair.inputs, xorTrainingPair.outputs, 0.01);
            }
            double mse = 0;
            for (XorTrainingPair xorTrainingPair : trainingData) {
                List<Double> prediction = feedForwardNeuralNetwork.predict(xorTrainingPair.inputs);
                double error = prediction.get(0) - xorTrainingPair.outputs.get(0);
                double errorSquared = error*error;
                mse += errorSquared;
            }
            System.out.println("RMSE: "+Math.sqrt(mse / trainingData.size()));
        }
    }

    private static class XorTrainingPair {
        public List<Double> inputs;
        public List<Double> outputs;

        public XorTrainingPair(double input1, double input2, double output) {
            inputs = Lists.newArrayList(input1, input2);
            outputs = Collections.singletonList(output);
        }
    }
}