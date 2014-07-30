package quickdt.data;

import quickdt.Label;

import java.io.Serializable;

public class Instance <L extends Serializable> extends AbstractInstance<L> implements Serializable {

    private static final long serialVersionUID = -932048363529904511L;

    private static final double DEFAULT_WEIGHT = 1.0;

    private Instance() {

	}

    public static <L extends Serializable> Instance<L> create(final Label<L> label, final Serializable... inputs) {
        return create(label, DEFAULT_WEIGHT, inputs);
    }

    public static <L extends Serializable>  Instance create(final Label<L> label, final double weight, final Serializable... inputs) {
		final HashMapAttributes a = new HashMapAttributes();
		for (int x = 0; x < inputs.length; x += 2) {
			a.put((String) inputs[x], inputs[x + 1]);
		}
		return new Instance<L>(a, label, weight);
	}

    public Instance(final Attributes attributes, final Label<L> label) {
        this(attributes, label, DEFAULT_WEIGHT);
    }

    public Instance(final Attributes attributes, final Label<L> label, final double weight) {
		this.attributes = attributes;
		this.label = label;
        this.weight = weight;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public Label<L> getLabel() {
        return label;
    }

    public Instance reweight(double newWeight){
        return new Instance(getAttributes(), getLabel(), newWeight);
    }

    @Override
    public double getWeight() {
        return weight;
    }

    private Attributes attributes;
	private Label<L> label;
    private double weight;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Instance instance = (Instance) o;

        if (Double.compare(instance.weight, weight) != 0) return false;
        if (!attributes.equals(instance.attributes)) return false;
        if (!label.equals(instance.label)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = attributes.hashCode();
        result = 31 * result + label.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
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
