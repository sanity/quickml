package quickdt.data;

import java.io.Serializable;
import java.util.Map;

public class InstanceWithMapOfRegressors extends AbstractInstance<Map<String, Serializable>> implements Serializable {

    private static final long serialVersionUID = -932048363529904511L;

    private static final double DEFAULT_WEIGHT = 1.0;

    private InstanceWithMapOfRegressors() {

	}

    public static InstanceWithMapOfRegressors create(final Serializable label, final Serializable... inputs) {
        return create(label, DEFAULT_WEIGHT, inputs);
    }

    public static InstanceWithMapOfRegressors create(final Serializable label, final double weight, final Serializable... inputs) {
		final HashMapAttributes a = new HashMapAttributes();
		for (int x = 0; x < inputs.length; x += 2) {
			a.put((String) inputs[x], inputs[x + 1]);
		}
		return new InstanceWithMapOfRegressors(a, label, weight);
	}

    public InstanceWithMapOfRegressors(final Map<String, Serializable> attributes, final Serializable label) {
        this(attributes, label, DEFAULT_WEIGHT);
    }

    public InstanceWithMapOfRegressors(final Map<String, Serializable> attributes, final Serializable label, final double weight) {
		this.attributes = attributes;
		this.label = label;
        this.weight = weight;
    }

    @Override
    public Map<String, Serializable> getRegressors() {
        return attributes;
    }

    @Override
    public Serializable getLabel() {
        return label;
    }

    public InstanceWithMapOfRegressors reweight(double newWeight){
        return new InstanceWithMapOfRegressors(getRegressors(), getLabel(), newWeight);
    }

    @Override
    public double getWeight() {
        return weight;
    }

    private Map<String, Serializable> attributes;
	private Serializable label;
    private double weight;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstanceWithMapOfRegressors instance = (InstanceWithMapOfRegressors) o;

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
