package quickdt;

import java.io.Serializable;

public class Instance extends AbstractInstance {

	private Instance() {

	}

    public static Instance create(final String classification, final Serializable... inputs) {
        return create(classification, 1.0, inputs);
    }

    public static Instance create(final String classification, final double weight, final Serializable... inputs) {
		final HashMapAttributes a = new HashMapAttributes();
		for (int x = 0; x < inputs.length; x += 2) {
			a.put((String) inputs[x], inputs[x + 1]);
		}
		return new Instance(a, classification);
	}

    public Instance(final Attributes attributes, final Serializable classification) {
        this(attributes, classification, 1.0);
    }

    public Instance(final Attributes attributes, final Serializable classification, final double weight) {
		this.attributes = attributes;
		this.classification = classification;
        this.weight = weight;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public Serializable getClassification() {
        return classification;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    private Attributes attributes;
	private Serializable classification;
    private double weight;

    @Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[attributes=");
		builder.append(attributes);
		builder.append(", classification=");
		builder.append(classification);
        if (weight != 1.0) {
            builder.append(", weight=");
            		builder.append(weight);
        }
		builder.append("]");
		return builder.toString();
	}
}
