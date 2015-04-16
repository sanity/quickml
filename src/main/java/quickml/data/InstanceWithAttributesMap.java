package quickml.data;


public class InstanceWithAttributesMap<L> implements Instance<AttributesMap, L> {

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

}
