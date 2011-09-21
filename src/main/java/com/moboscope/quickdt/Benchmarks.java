package com.moboscope.quickdt;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.json.simple.*;

import com.google.common.collect.*;
import com.moboscope.quickdt.TreeBuilder.Scorer;
import com.moboscope.quickdt.scorers.Scorer1;

public class Benchmarks {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(new FileInputStream(
				new File(new File(System.getProperty("user.dir")), "testdata/mobo1.txt.gz"))))));

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

		for (final Scorer scorer : Sets.newHashSet(new Scorer1())) {
			final TreeBuilder tb = new TreeBuilder(scorer);

			final long startTime = System.currentTimeMillis();
			final Node tree = tb.buildTree(instances, 100, 1.0);
			System.out.println(scorer.getClass().getSimpleName() + " build time "
					+ (System.currentTimeMillis() - startTime) + ", size: " + tree.size() + " mean depth: "
					+ tree.meanDepth());
		}
	}

}
