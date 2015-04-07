package quickml.data;


public class InstanceWithAttributesMap implements Instance<AttributesMap, Object> {

    private AttributesMap attributes;
    private Object label;
    private double weight;

    private InstanceWithAttributesMap() {

    }

    public InstanceWithAttributesMap(AttributesMap attributes, Object label) {
        this(attributes, label, 1.0);
    }

    public InstanceWithAttributesMap(AttributesMap attributes, Object label, double weight) {
        this.attributes = attributes;
        this.label = label;
        this.weight = weight;
    }

    @Override
    public AttributesMap getAttributes() {
        return attributes;
    }

    @Override
    public Object getLabel() {
        return label;
    }

    @Override
    public double getWeight() {
        return weight;
    }

}
