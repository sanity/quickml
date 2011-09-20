package com.moboscope.quickdt;

import java.io.Serializable;

public class Instance {

	private Instance() {

	}

	public static Instance create(final String output, final Serializable... inputs) {
		final Attributes a = new Attributes();
		for (int x = 0; x < inputs.length; x += 2) {
			a.put((String) inputs[x], inputs[x + 1]);
		}
		return new Instance(a, output);
	}

	public Instance(final Attributes attributes, final Serializable output) {
		this.attributes = attributes;
		this.output = output;
	}

	public Attributes attributes;
	public Serializable output;

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[attributes=");
		builder.append(attributes);
		builder.append(", output=");
		builder.append(output);
		builder.append("]");
		return builder.toString();
	}
}
