package quickml.data;

import java.io.Serializable;

public class InstanceWithAttributesMap implements Instance<AttributesMap, Serializable> {

    private AttributesMap attributes;
    private Serializable label;
    private double weight;

    private InstanceWithAttributesMap() {

    }

    public InstanceWithAttributesMap(AttributesMap attributes, Serializable label) {
        this(attributes, label, 1.0);
    }

    public InstanceWithAttributesMap(AttributesMap attributes, Serializable label, double weight) {
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
