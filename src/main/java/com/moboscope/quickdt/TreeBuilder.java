package com.moboscope.quickdt;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import org.javatuples.Pair;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.moboscope.quickdt.scorers.Scorer1;
import com.uprizer.sensearray.freetools.stats.ReservoirSampler;

public class TreeBuilder {

	Scorer scorer;

	public TreeBuilder() {
		this(new Scorer1());
	}

	public TreeBuilder(final Scorer scorer) {
		this.scorer = scorer;
	}

	public static final int ORDINAL_TEST_SPLITS = 5;

	public Node buildTree(final Iterable<Instance> trainingData, final int maxDepth, final double minProbability) {
		final Map<String, ReservoirSampler<Double>> rsm = Maps.newHashMap();

		for (final Instance i : trainingData) {
			for (final Entry<String, Serializable> e : i.attributes.entrySet()) {
				if (e.getValue() instanceof Number) {
					ReservoirSampler<Double> rs = rsm.get(e.getKey());
					if (rs == null) {
						rs = new ReservoirSampler<Double>(1000);
						rsm.put(e.getKey(), rs);
					}
					rs.addSample(((Number) e.getValue()).doubleValue());
				}
			}
		}

		final Map<String, double[]> splits = Maps.newHashMap();

		for (final Entry<String, ReservoirSampler<Double>> e : rsm.entrySet()) {
			final ArrayList<Double> al = Lists.newArrayList();
			for (final Double d : e.getValue().getSamples()) {
				al.add(d);
			}
			Collections.sort(al);

			final double[] split = new double[ORDINAL_TEST_SPLITS - 1];
			for (int x = 0; x < split.length; x++) {
				split[x] = al.get((x + 1) * al.size() / (split.length + 2));
			}

			splits.put(e.getKey(), split);
		}

		return buildTree(trainingData, 0, maxDepth, minProbability, splits);
	}

	public Node buildTree(final Iterable<Instance> trainingData, final int depth, final int maxDepth,
			final double minProbability, final Map<String, double[]> splits) {
		final Leaf thisLeaf = new Leaf(trainingData, depth);
		if (depth == maxDepth || thisLeaf.label.probability >= minProbability)
			return thisLeaf;

		final Instance sampleInstance = Iterables.get(trainingData, 0);

		Branch bestNode = null;
		double bestScore = 0;
		for (final Entry<String, Serializable> e : sampleInstance.attributes.entrySet()) {
			Pair<? extends Branch, Double> thisPair;
			if (e.getValue() instanceof Number) {
				thisPair = createOrdinalNode(e.getKey(), trainingData, splits.get(e.getKey()));
			} else {
				thisPair = createNominalNode(e.getKey(), trainingData);
			}
			if (thisPair.getValue1() > bestScore) {
				bestScore = thisPair.getValue1();
				bestNode = thisPair.getValue0();
			}
		}

		if (bestNode == null) {
			final StringBuilder sb = new StringBuilder();
			for (final Instance i : trainingData) {
				sb.append(i.toString());
			}
			return thisLeaf;
		}

		bestNode.trueChild = buildTree(Lists.newLinkedList(Iterables.filter(trainingData, bestNode.getInPredicate())),
				depth + 1, maxDepth, minProbability, splits);

		bestNode.falseChild = buildTree(
				Lists.newLinkedList(Iterables.filter(trainingData, bestNode.getOutPredicate())), depth + 1,
				maxDepth,
				minProbability, splits);

		return bestNode;
	}

	public Pair<? extends Branch, Double> createOrdinalNode(final String attribute, final Iterable<Instance> instances,
			final double[] splits) {

		double bestScore = 0;
		double bestThreshold = 0;

		double lastThreshold = Double.MIN_VALUE;
		for (final double threshold : splits) {
			// Sometimes we can get a few thresholds the same, avoid wasted
			// effort when we do
			if (threshold == lastThreshold) {
				continue;
			}
			lastThreshold = threshold;
			final Iterable<Instance> inSet = Iterables.filter(instances, new Predicate<Instance>() {

				@Override
				public boolean apply(final Instance input) {
					return ((Number) input.attributes.get(attribute)).doubleValue() > threshold;
				}
			});
			final Iterable<Instance> outSet = Iterables.filter(instances, new Predicate<Instance>() {

				@Override
				public boolean apply(final Instance input) {
					return ((Number) input.attributes.get(attribute)).doubleValue() <= threshold;
				}
			});
			final Pair<Integer, Map<Serializable, Integer>> inOutcomeCounts = calcOutcomeCounts(inSet);
			final Pair<Integer, Map<Serializable, Integer>> outOutcomeCounts = calcOutcomeCounts(outSet);

			final double thisScore = scorer.scoreSplit(inOutcomeCounts.getValue0(), inOutcomeCounts.getValue1(),
					outOutcomeCounts.getValue0(), outOutcomeCounts.getValue1());

			if (thisScore > bestScore) {
				bestScore = thisScore;
				bestThreshold = threshold;
			}
		}

		return Pair.with(new OrdinalBranch(attribute, bestThreshold), bestScore);
	}

	public Pair<? extends Branch, Double> createNominalNode(final String attribute,
			final Iterable<Instance> instances) {
		final Set<Serializable> values = Sets.newHashSet();
		for (final Instance instance : instances) {
			values.add(instance.attributes.get(attribute));
		}
		double score = 0;
		final Set<Serializable> bestSoFar = Sets.newHashSet();
		while (true) {
			double bestScore = 0;
			Serializable bestVal = null;
			for (final Serializable testVal : values) {
				final Set<Serializable> testSet = Sets.newHashSet(bestSoFar);
				testSet.add(testVal);

				final Iterable<Instance> inSet = Iterables.filter(instances, new Predicate<Instance>() {

					@Override
					public boolean apply(final Instance input) {
						return testSet.contains(input.attributes.get(attribute));
					}
				});
				final Iterable<Instance> outSet = Iterables.filter(instances, new Predicate<Instance>() {

					@Override
					public boolean apply(final Instance input) {
						return !testSet.contains(input.attributes.get(attribute));
					}
				});

				final Pair<Integer, Map<Serializable, Integer>> inOutcomeCounts = calcOutcomeCounts(inSet);
				final Pair<Integer, Map<Serializable, Integer>> outOutcomeCounts = calcOutcomeCounts(outSet);

				final double thisScore = scorer.scoreSplit(inOutcomeCounts.getValue0(), inOutcomeCounts.getValue1(),
						outOutcomeCounts.getValue0(), outOutcomeCounts.getValue1());

				if (thisScore > bestScore) {
					bestScore = thisScore;
					bestVal = testVal;
				}
			}
			if (bestScore > score) {
				score = bestScore;
				bestSoFar.add(bestVal);
			} else {
				break;
			}
		}

		return Pair.with(new NominalBranch(attribute, bestSoFar), score);
	}

	public static Pair<Integer, Map<Serializable, Integer>> calcOutcomeCounts(final Iterable<Instance> instances) {
		final Map<Serializable, Integer> outcomeCounts = Maps.newHashMap();
		int ttl = 0;
		for (final Instance instance : instances) {
			final Serializable value = instance.output;
			Integer c = outcomeCounts.get(value);
			if (c == null) {
				c = 0;
			}
			outcomeCounts.put(value, c + 1);
			ttl++;
		}
		return Pair.with(ttl, outcomeCounts);
	}

	public static interface Scorer {
		public double scoreSplit(final int aTtl, final Map<Serializable, Integer> a, final int bTtl,
				final Map<Serializable, Integer> b);
	}
}
