package quickdt;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.uprizer.sensearray.freetools.stats.ReservoirSampler;
import org.javatuples.Pair;
import quickdt.scorers.Scorer1;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public final class TreeBuilder implements PredictiveModelBuilder<Tree> {

	public static final int ORDINAL_TEST_SPLITS = 5;

    private int maxDepth = Integer.MAX_VALUE;
    private double minProbability = 1.0;
    private int attributeExcludeDepth = 1;
    private Set<String> excludeAttributes = Collections.<String>emptySet();

	Scorer scorer;
    private Set<String> excludeAttributeFromTopLevel;

    public TreeBuilder maxDepth(int maxDepth) { this.maxDepth=maxDepth; return this; }
    public TreeBuilder minProbability(double minProbability) { this.minProbability=minProbability; return this; }
    public TreeBuilder attributeExcludeDepth(int depth) { this.attributeExcludeDepth=depth; return this; }
    public TreeBuilder excludeAttributes(Set<String> attributes) { this.excludeAttributes = attributes; return this; }

    public TreeBuilder() {
		this(new Scorer1());
	}

	public TreeBuilder(final Scorer scorer) {
		this.scorer = scorer;
	}

    @Override
	public Tree buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return new Tree(buildTree(null, trainingData, 0, createOrdinalSplits(trainingData)));
	}

	private double[] createOrdinalSplit(final Iterable<? extends AbstractInstance> trainingData, final String attribute) {
		final ReservoirSampler<Double> rs = new ReservoirSampler<Double>(1000);
		for (final AbstractInstance i : trainingData) {
			rs.addSample(((Number) i.getAttributes().get(attribute)).doubleValue());
		}
		final ArrayList<Double> al = Lists.newArrayList();
		for (final Double d : rs.getSamples()) {
			al.add(d);
		}
		Collections.sort(al);

		final double[] split = new double[ORDINAL_TEST_SPLITS - 1];
		for (int x = 0; x < split.length; x++) {
			split[x] = al.get((x + 1) * al.size() / (split.length + 1));
		}

		return split;
	}

	private Map<String, double[]> createOrdinalSplits(final Iterable<? extends AbstractInstance> trainingData) {
		final Map<String, ReservoirSampler<Double>> rsm = Maps.newHashMap();
		for (final AbstractInstance i : trainingData) {
			for (final Entry<String, Serializable> e : i.getAttributes().entrySet()) {
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

	protected Node buildTree(Node parent, final Iterable<? extends AbstractInstance> trainingData, final int depth,
                             final Map<String, double[]> splits) {
		final Leaf thisLeaf = new Leaf(parent, trainingData, depth);
		if (depth == maxDepth || thisLeaf.getBestClassificationProbability() >= minProbability)
			return thisLeaf;

		final AbstractInstance sampleInstance = Iterables.get(trainingData, 0);

		boolean smallTrainingSet = true;
		int tsCount = 0;
		for (final AbstractInstance i : trainingData) {
			tsCount++;
			if (tsCount > 10) {
				smallTrainingSet = false;
				break;
			}
		}

		Branch bestNode = null;
		double bestScore = 0;
		for (final Entry<String, Serializable> e : sampleInstance.getAttributes().entrySet()) {
            if (depth <= attributeExcludeDepth && excludeAttributes.contains(e.getKey())) {
                continue;
            }

			Pair<? extends Branch, Double> thisPair = null;

			if (!smallTrainingSet && e.getValue() instanceof Number) {
				thisPair = createOrdinalNode(parent, e.getKey(), trainingData, splits.get(e.getKey()));
			}

			if (thisPair == null || thisPair.getValue1() == 0) {
				thisPair = createNominalNode(parent, e.getKey(), trainingData);
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

		final LinkedList<? extends AbstractInstance> trueTrainingSet = Lists.newLinkedList(Iterables.filter(trainingData,
				bestNode.getInPredicate()));
		final LinkedList<? extends AbstractInstance> falseTrainingSet = Lists.newLinkedList(Iterables.filter(trainingData,
				bestNode.getOutPredicate()));

		// We want to temporarily replace the split for an attribute for
		// descendants of an ordinal branch, first the true split
		if (bestNode instanceof OrdinalBranch) {
			final OrdinalBranch ob = (OrdinalBranch) bestNode;
			oldSplit = splits.get(ob.attribute);
			splits.put(ob.attribute, createOrdinalSplit(trueTrainingSet, ob.attribute));
		}

		// Recurse down the true branch
		bestNode.trueChild = buildTree(bestNode, trueTrainingSet, depth + 1, splits);

		// And now replace the old split if this is an OrdinalBranch
		if (bestNode instanceof OrdinalBranch) {
			final OrdinalBranch ob = (OrdinalBranch) bestNode;
			splits.put(ob.attribute, createOrdinalSplit(falseTrainingSet, ob.attribute));
		}

		// Recurse down the false branch
		bestNode.falseChild = buildTree(bestNode, falseTrainingSet, depth + 1, splits);

		// And now replace the original split if this is an OrdinalBranch
		if (bestNode instanceof OrdinalBranch) {
			final OrdinalBranch ob = (OrdinalBranch) bestNode;
			splits.put(ob.attribute, oldSplit);
		}

		return bestNode;
	}

	protected Pair<? extends Branch, Double> createNominalNode(Node parent, final String attribute,
			final Iterable<? extends AbstractInstance> instances) {
		final Set<Serializable> values = Sets.newHashSet();
		for (final AbstractInstance instance : instances) {
			values.add(instance.getAttributes().get(attribute));
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
				if (testValCounts == null) { // Also a kludge, figure out why
					// this would happen
					continue;
				}
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

		return Pair.with(new NominalBranch(parent, attribute, bestSoFar), score);
	}

	protected Pair<? extends Branch, Double> createOrdinalNode(Node parent, final String attribute,
			final Iterable<? extends AbstractInstance> instances,
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
			final Iterable<? extends AbstractInstance> inSet = Iterables.filter(instances, new Predicate<AbstractInstance>() {

				@Override
				public boolean apply(final AbstractInstance input) {
					try {
						return ((Number) input.getAttributes().get(attribute)).doubleValue() > threshold;
					} catch (final ClassCastException e) { // Kludge, need to
						// handle better
						return false;
					}
				}
			});
			final Iterable<? extends AbstractInstance> outSet = Iterables.filter(instances, new Predicate<AbstractInstance>() {

				@Override
				public boolean apply(final AbstractInstance input) {
					try {
						return ((Number) input.getAttributes().get(attribute)).doubleValue() <= threshold;
					} catch (final ClassCastException e) { // Kludge, need to
						// handle better
						return false;
					}
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

		return Pair.with(new OrdinalBranch(parent, attribute, bestThreshold), bestScore);
	}


}
