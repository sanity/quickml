package quickml.data;

import java.io.Serializable;

public class ClassifierInstance implements Instance<AttributesMap, Serializable> {

    private AttributesMap attributes;
    private Serializable label;
    private double weight;

    private ClassifierInstance() {

    }

    public ClassifierInstance(AttributesMap attributes, Serializable label) {
        this(attributes, label, 1.0);
    }

    public ClassifierInstance(AttributesMap attributes, Serializable label, double weight) {
        this.attributes = attributes;
        this.label = label;
        this.weight = weight;
    }

    @Override
    public AttributesMap getAttributes() {
        return attributes;
    }

    @Override
    public Serializable getLabel() {
        return label;
    }

    @Override
    public double getWeight() {
        return weight;
    }

}
