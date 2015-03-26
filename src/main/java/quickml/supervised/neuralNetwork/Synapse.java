package quickml.supervised.neuralNetwork;

/**
 * Created by ian on 12/23/14.
 */
public class Synapse {
    final Neuron a, b;
    double weight;

    private Synapse(Neuron a, Neuron b) {
        this.a = a;
        this.b = b;
    }

    public static Synapse CONNECT(Neuron a, Neuron b) {
        Synapse synapse = new Synapse(a, b);
        a.getOutputs().add(synapse);
        b.getInputs().add(synapse);
        return synapse;
    }

    public Neuron getA() {
        return a;
    }

    public Neuron getB() {
        return b;
    }

    public double getWeight() {
        return weight;
    }

    public void updateWeight(double newWeight) {
        weight = newWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Synapse synapse = (Synapse) o;

        if (!a.equals(synapse.a)) return false;
        if (!b.equals(synapse.b)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }
}
