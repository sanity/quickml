package quickml.data.instances;


import quickml.data.AttributesMap;

import java.io.Serializable;

public class InstanceWithAttributesMap<L extends Serializable> implements Instance<AttributesMap, L> {

    private AttributesMap attributes;
    private L label;
    private double weight;

    private InstanceWithAttributesMap() {

    }

    public InstanceWithAttributesMap(AttributesMap attributes, L label) {
        this(attributes, label, 1.0);
    }

    public InstanceWithAttributesMap(AttributesMap attributes, L label, double weight) {
        this.attributes = attributes;
        this.label = label;
        this.weight = weight;
    }

    @Override
    public AttributesMap getAttributes() {
        return attributes;
    }

    @Override
    public L getLabel() {
        return label;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstanceWithAttributesMap<L> instance = (InstanceWithAttributesMap) o;

        if (Double.compare(instance.weight, weight) != 0) return false;
        if (!attributes.equals(instance.attributes)) return false;
        if (!label.equals(instance.label)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        result = attributes.hashCode();
        result = 31 * result + label.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[attributes=");
        builder.append(attributes);
        builder.append(", label=");
        builder.append(label);
        if (weight != 1.0) {
            builder.append(", weight=");
            builder.append(weight);
        }
        builder.append("]");
        return builder.toString();
    }
}
