package com.moboscope.quickdt;

import java.io.*;

public abstract class Node implements Serializable {
	public abstract Label getLabel(Attributes attributes);

	public abstract int size();

	public void dump(final PrintStream ps) {
		dump(0, ps);
	}

	public abstract void dump(int indent, PrintStream ps);

	public static class Label implements Serializable {
		private static final long serialVersionUID = -4063175796311033721L;

		public Label(final Serializable output, final int depth, final int exampleCount, final double probability) {
			this.output = output;
			this.depth = depth;
			this.exampleCount = exampleCount;
			this.probability = probability;
		}

		public final int exampleCount;

		public Serializable output;

		public int depth;

		public double probability;

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("[output=");
			builder.append(output);
			builder.append(", depth=");
			builder.append(depth);
			builder.append(", exampleCount=");
			builder.append(exampleCount);
			builder.append(", probability=");
			builder.append(probability);
			builder.append("]");
			return builder.toString();
		}

	}
}
