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

	public static final int ORDINAL_TEST_SPLITS = 5;

	Scorer scorer;

	public TreeBuilder() {
		this(new Scorer1());
	}

	public TreeBuilder(final Scorer scorer) {
		this.scorer = scorer;
	}

	public Node buildTree(final Iterable<Instance> trainingData) {
		return buildTree(trainingData, Integer.MAX_VALUE, 1.0);
	}

	public Node buildTree(final Iterable<Instance> trainingData, final int maxDepth, final double minProbability) {
		return buildTree(trainingData, 0, maxDepth, minProbability, createOrdinalSplits(trainingData));
	}


	private double[] createOrdinalSplit(final Iterable<Instance> trainingData, final String attribute) {
		final ReservoirSampler<Double> rs = new ReservoirSampler<Double>(1000);
		for (final Instance i : trainingData) {
			rs.addSample(((Number) i.attributes.get(attribute)).doubleValue());
		}
		final ArrayList<Double> al = Lists.newArrayList();
		for (final Double d : rs.getSamples()) {
			al.add(d);
		}
		Collections.sort(al);

		final double[] split = new double[ORDINAL_TEST_SPLITS - 1];
		for (int x = 0; x < split.length; x++) {
			split[x] = al.get((x + 1) * al.size() / (split.length + 2));
		}

		return split;
	}

	private Map<String, double[]> createOrdinalSplits(final Iterable<Instance> trainingData) {
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
		return splits;
	}

	protected Node buildTree(final Iterable<Instance> trainingData, final int depth, final int maxDepth,
			final double minProbability, final Map<String, double[]> splits) {
		final Leaf thisLeaf = new Leaf(trainingData, depth);
		if (depth == maxDepth || thisLeaf.probability >= minProbability)
			return thisLeaf;

		final Instance sampleInstance = Iterables.get(trainingData, 0);

		boolean smallTrainingSet = true;
		int tsCount = 0;
		for (final Instance i : trainingData) {
			tsCount++;
			if (tsCount > 10) {
				smallTrainingSet = false;
				break;
			}
		}

		Branch bestNode = null;
		double bestScore = 0;
		for (final Entry<String, Serializable> e : sampleInstance.attributes.entrySet()) {
			Pair<? extends Branch, Double> thisPair = null;

			if (!smallTrainingSet && e.getValue() instanceof Number) {
				thisPair = createOrdinalNode(e.getKey(), trainingData, splits.get(e.getKey()));
			}

			if (thisPair == null || thisPair.getValue1() == 0) {
				thisPair = createNominalNode(e.getKey(), trainingData);
			}
			if (thisPair.getValue1() > bestScore) {
				bestScore = thisPair.getValue1();
				bestNode = thisPair.getValue0();
			}
		}

		// If we were unable to find a useful branch, return the leaf
		if (bestNode == null)
			// Its a bad sign when this happens, normally something to debug
			return thisLeaf;

		double[] oldSplit = null;

		final LinkedList<Instance> trueTrainingSet = Lists.newLinkedList(Iterables.filter(trainingData,
				bestNode.getInPredicate()));
		final LinkedList<Instance> falseTrainingSet = Lists.newLinkedList(Iterables.filter(trainingData,
				bestNode.getOutPredicate()));

		// We want to temporarily replace the split for an attribute for
		// descendants of an ordinal branch, first the true split
		if (bestNode instanceof OrdinalBranch) {
			final OrdinalBranch ob = (OrdinalBranch) bestNode;
			oldSplit = splits.get(ob.attribute);
			splits.put(ob.attribute, createOrdinalSplit(trueTrainingSet, ob.attribute));
		}

		// Recurse down the true branch
		bestNode.trueChild = buildTree(trueTrainingSet, depth + 1, maxDepth, minProbability, splits);

		// And now replace the old split if this is an OrdinalBranch
		if (bestNode instanceof OrdinalBranch) {
			final OrdinalBranch ob = (OrdinalBranch) bestNode;
			splits.put(ob.attribute, createOrdinalSplit(falseTrainingSet, ob.attribute));
		}

		// Recurse down the false branch
		bestNode.falseChild = buildTree(
				falseTrainingSet, depth + 1,
				maxDepth,
				minProbability, splits);

		// And now replace the original split if this is an OrdinalBranch
		if (bestNode instanceof OrdinalBranch) {
			final OrdinalBranch ob = (OrdinalBranch) bestNode;
			splits.put(ob.attribute, oldSplit);
		}

		return bestNode;
	}

	protected Pair<? extends Branch, Double> createNominalNode(final String attribute,
			final Iterable<Instance> instances) {
		final Set<Serializable> values = Sets.newHashSet();
		for (final Instance instance : instances) {
			values.add(instance.attributes.get(attribute));
		}
		double score = 0;
		final Set<Serializable> bestSoFar = Sets.newHashSet();

		ClassificationCounter inCounts = new ClassificationCounter();
		final Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> valueOutcomeCountsPair = ClassificationCounter
				.countAllByAttributeValues(instances, attribute);
		ClassificationCounter outCounts = valueOutcomeCountsPair.getValue0();
		final Map<Serializable, ClassificationCounter> valueOutcomeCounts = valueOutcomeCountsPair.getValue1();

		while (true) {
			double bestScore = 0;
			Serializable bestVal = null;
			for (final Serializable testVal : values) {
				final ClassificationCounter testValCounts = valueOutcomeCounts.get(testVal);
				final ClassificationCounter testInCounts = inCounts.add(testValCounts);
				final ClassificationCounter testOutCounts = outCounts.subtract(testValCounts);


				final double thisScore = scorer.scoreSplit(testInCounts, testOutCounts);

				if (thisScore > bestScore) {
					bestScore = thisScore;
					bestVal = testVal;
				}
			}
			if (bestScore > score) {
				score = bestScore;
				bestSoFar.add(bestVal);
				values.remove(bestVal);
				final ClassificationCounter bestValOutcomeCounts = valueOutcomeCounts.get(bestVal);
				inCounts = inCounts.add(bestValOutcomeCounts);
				outCounts = outCounts.subtract(bestValOutcomeCounts);
			} else {
				break;
			}
		}

		return Pair.with(new NominalBranch(attribute, bestSoFar), score);
	}

	protected Pair<? extends Branch, Double> createOrdinalNode(final String attribute,
			final Iterable<Instance> instances,
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
			final ClassificationCounter inClassificationCounts = ClassificationCounter.countAll(inSet);
			final ClassificationCounter outClassificationCounts = ClassificationCounter.countAll(outSet);

			final double thisScore = scorer.scoreSplit(inClassificationCounts, outClassificationCounts);

			if (thisScore > bestScore) {
				bestScore = thisScore;
				bestThreshold = threshold;
			}
		}

		return Pair.with(new OrdinalBranch(attribute, bestThreshold), bestScore);
	}
}
