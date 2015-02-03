package quickml.supervised.neuralNetwork;

import quickml.supervised.PredictiveModelBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ian on 1/15/15.
 */
public class FeedForwardNeuralNetworkBuilder implements PredictiveModelBuilder<FeedForwardNeuralNetwork, NeuralNetInstance> {
    private List<Integer> layerSizes;
    private double learningRate;
    private StopTrainingDecider stopTrainingDecider;

    public FeedForwardNeuralNetworkBuilder(List<Integer> layerSizes, double learningRate, int cycles) {
        this(layerSizes, learningRate, new FixedTrainingCyclesDecider(cycles));
    }

    public FeedForwardNeuralNetworkBuilder(List<Integer> layerSizes, double learningRate, StopTrainingDecider stopTrainingDecider) {
        this.layerSizes = layerSizes;
        this.learningRate = learningRate;
        this.stopTrainingDecider = stopTrainingDecider;
    }

    @Override
    public FeedForwardNeuralNetwork buildPredictiveModel(Iterable<NeuralNetInstance> trainingData) {
        FeedForwardNeuralNetwork neuralNetwork = new FeedForwardNeuralNetwork(layerSizes);
        while (true) {
            for (NeuralNetInstance neuralNetInstance : trainingData) {
                neuralNetwork.updateWeightsAndBiases(neuralNetInstance.getAttributes(), neuralNetInstance.getLabel(), learningRate);
            }
            if (stopTrainingDecider.shouldStopTraining(neuralNetwork)) {
                break;
            }
        }
        return neuralNetwork;
    }

    @Override
    public void updateBuilderConfig(Map<String, Object> config) {
        throw new UnsupportedOperationException();
    }

    public static interface StopTrainingDecider {
        public boolean shouldStopTraining(FeedForwardNeuralNetwork neuralNetwork);
    }

    public static class FixedTrainingCyclesDecider implements StopTrainingDecider {
        private int trainingCycles;
        AtomicInteger cycles = new AtomicInteger(0);

        public FixedTrainingCyclesDecider(int trainingCycles) {
            this.trainingCycles = trainingCycles;
        }

        @Override
        public boolean shouldStopTraining(FeedForwardNeuralNetwork neuralNetwork) {
            int cycleCount = cycles.incrementAndGet();
            return cycleCount >= trainingCycles;
        }
    }
}
