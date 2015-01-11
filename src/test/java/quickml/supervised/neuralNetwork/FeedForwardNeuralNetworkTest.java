package quickml.supervised.neuralNetwork;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

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
}