package quickdt;

import java.io.Serializable;

public class Instance {

	private Instance() {

	}

    public static Instance create(final String classification, final Serializable... inputs) {
        return create(classification, 1.0, inputs);
    }

    public static Instance create(final String classification, final double weight, final Serializable... inputs) {
		final Attributes a = new Attributes();
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

	public Attributes attributes;
	public Serializable classification;
    public double weight;

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
