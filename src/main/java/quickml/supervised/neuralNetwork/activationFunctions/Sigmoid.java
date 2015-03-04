package quickml.supervised.neuralNetwork.activationFunctions;

import quickml.supervised.neuralNetwork.ActivationFunction;

/**
 * Created by ian on 12/27/14.
 */
public class Sigmoid implements ActivationFunction {
    public static Sigmoid SINGLETON = new Sigmoid();

    @Override
    public double apply(double input) {
        return 1 / (1 + Math.exp(-input));
    }
}
