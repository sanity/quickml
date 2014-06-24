package quickdt.predictiveModels.decisionTree.tree;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.Attributes;

import java.io.Serializable;

public final class NumericBranch extends Branch {
    private static final  Logger logger =  LoggerFactory.getLogger(NumericBranch.class);

	private static final long serialVersionUID = 4456176008067679801L;
	public final double threshold;

	public NumericBranch(Node parent, final String attribute, final double threshold) {
		super(parent, attribute);
		this.threshold = threshold;

	}

	@Override
	public boolean decide(final Attributes attributes) {
        Serializable value = attributes.get(attribute);
        if (value == null) value = 0;
        if (!(value instanceof Number)) {
            throw new RuntimeException("Expecting a number as the value of "+attribute+" but got "+value +" of type "+value.getClass().getSimpleName());
        }
        final double valueAsDouble = ((Number) value).doubleValue();
		return valueAsDouble > threshold;
	}

	@Override
	public String toString() {
		return attribute + " > " + threshold;
	}

	@Override
	public String toNotString() {
		return attribute + " <= " + threshold;

	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final NumericBranch that = (NumericBranch) o;

        if (Double.compare(that.threshold, threshold) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(threshold);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
