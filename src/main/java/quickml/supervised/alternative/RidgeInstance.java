package quickml.supervised.alternative;

import org.joda.time.DateTime;
import quickml.data.Instance;

import java.io.Serializable;

public class RidgeInstance implements Instance<double[], Serializable>{

    private double[] attributes;
    private Serializable label;

    public RidgeInstance(double[] attributes, Serializable label) {
        this.attributes = attributes;
        this.label = label;
    }

    @Override
    public double[] getAttributes() {
        return attributes;
    }

    @Override
    public Serializable getLabel() {
        return label;
    }

    @Override
    public double getWeight() {
        return 1.0;
    }

    @Override
    public DateTime getTimestamp() {
        throw new RuntimeException("RidgeInstance does not support timestamp");
    }
}
