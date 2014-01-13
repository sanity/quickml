package quickdt;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public final class OrdinalBranch extends Branch {
    private static final  Logger logger =  LoggerFactory.getLogger(OrdinalBranch.class);

	private static final long serialVersionUID = 4456176008067679801L;
	public final double threshold;

	public OrdinalBranch(Node parent, final String attribute, final double threshold) {
		super(parent, attribute);
		this.threshold = threshold;

	}

	@Override
	protected boolean decide(final Attributes attributes) {
        final Serializable value = attributes.get(attribute);
        if (!(value instanceof Number)) {
            logger.error("Expecting a number as the value of "+attribute+" but got "+value);
            return false;
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
}
