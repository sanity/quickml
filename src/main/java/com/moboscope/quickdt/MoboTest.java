package com.moboscope.quickdt;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.json.simple.*;

import com.google.common.collect.Lists;

public class MoboTest {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(new FileInputStream(
				new File("/Users/ian/data/moboscope/training-20110919.txt.gz"))))));

		final List<Instance> instances = Lists.newLinkedList();

		int count = 0;
		while (true) {
			count++;
			final String line = br.readLine();
			if (line == null) {
				break;
			}
			final JSONObject jo = (JSONObject) JSONValue.parse(line);
			final Attributes a = new Attributes();
			a.putAll((JSONObject) jo.get("attributes"));
			instances.add(new Instance(a, (String) jo.get("output")));
		}

		System.out.println("Read " + instances.size() + " instances");

		final TreeBuilder tb = new TreeBuilder();

		final long startTime = System.currentTimeMillis();
		final Node tree = tb.buildTree(instances, 100, 1.0);
		System.out.println("Build time: " + (System.currentTimeMillis() - startTime));

		tree.dump(System.out);
	}

}
