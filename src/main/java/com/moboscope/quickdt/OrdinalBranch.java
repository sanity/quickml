package com.moboscope.quickdt;


public final class OrdinalBranch extends Branch {
	private static final long serialVersionUID = 4456176008067679801L;
	public final double threshold;

	public OrdinalBranch(final String attribute, final double threshold) {
		super(attribute);
		this.threshold = threshold;

	}

	@Override
	protected boolean decide(final Attributes attributes) {
		final double value = ((Number) attributes.get(attribute)).doubleValue();
		return value > threshold;
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
