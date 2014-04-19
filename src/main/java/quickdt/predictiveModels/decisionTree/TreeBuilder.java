package quickdt.predictiveModels.decisionTree;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.uprizer.sensearray.freetools.stats.ReservoirSampler;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Misc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.scorers.MSEScorer;
import quickdt.predictiveModels.decisionTree.tree.*;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public final class TreeBuilder implements PredictiveModelBuilder<Tree> {
    private static final Logger logger = LoggerFactory.getLogger(TreeBuilder.class);

    public static final int ORDINAL_TEST_SPLITS = 5;
    private final Scorer scorer;
    private int maxDepth = Integer.MAX_VALUE;
    private double ignoreAttributeAtNodeProbability = 0.0;
    private double minimumScore = 0.00000000000001;
    private int minCategoricalAttributeValueOccurances = 5;
    private int minLeafInstances = 0;

    public TreeBuilder() {
        this(new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE));
    }

    public TreeBuilder(final Scorer scorer) {
        this.scorer = scorer;
    }

    public TreeBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public TreeBuilder minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public TreeBuilder ignoreAttributeAtNodeProbability(double probability) {
        this.ignoreAttributeAtNodeProbability = probability;
        return this;
    }

    public TreeBuilder minCategoricalAttributeValueOccurances(int occurances) {
        this.minCategoricalAttributeValueOccurances = occurances;
        return this;
    }

    public TreeBuilder minimumScore(double minimumScore) {
        this.minimumScore = minimumScore;
        return this;
    }

    @Override
    public Tree buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return new Tree(buildTree(null, trainingData, 0, createNumericSplits(trainingData)));
    }

    private double[] createNumericSplit(final Iterable<? extends AbstractInstance> trainingData, final String attribute) {
     //   logger.debug("Creating numeric split for attribute {}", attribute);
        final ReservoirSampler<Double> rs = new ReservoirSampler<Double>(1000);
        for (final AbstractInstance i : trainingData) {
            rs.addSample(((Number) i.getAttributes().get(attribute)).doubleValue());
        }
        final ArrayList<Double> al = Lists.newArrayList();
        for (final Double d : rs.getSamples()) {
            al.add(d);
        }
        if (al.isEmpty()) {
            throw new RuntimeException("Split list empty");
        }
        Collections.sort(al);

        final double[] split = new double[ORDINAL_TEST_SPLITS - 1];
        for (int x = 0; x < split.length; x++) {
            split[x] = al.get((x + 1) * al.size() / (split.length + 1));
        }

//        logger.debug("Created numeric split for attribute {}: {}", attribute, Arrays.toString(split));
        return split;
    }

    private Map<String, double[]> createNumericSplits(final Iterable<? extends AbstractInstance> trainingData) {
     //   logger.debug("Creating numeric splits");
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
        Preconditions.checkArgument(!Iterables.isEmpty(trainingData), "At Depth: " + depth +". Can't build a tree with no training data" );
        final Leaf thisLeaf = new Leaf(parent, trainingData, depth);

        if (depth >= maxDepth) {
            return thisLeaf;
        }

        //should not be doing the following operation every time we call buildTree
        Map<String, AttributeCharacteristics> attributeCharacteristics = surveyTrainingData(trainingData);

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
        for (final Entry<String, AttributeCharacteristics> e : attributeCharacteristics.entrySet()) {
            if (this.ignoreAttributeAtNodeProbability > 0 && Misc.random.nextDouble() < this.ignoreAttributeAtNodeProbability)
                continue;

            Pair<? extends Branch, Double> thisPair = null;

            if (!smallTrainingSet && e.getValue().isNumber) {
                thisPair = createNumericNode(parent, e.getKey(), trainingData, splits.get(e.getKey()));
            }

            if (thisPair == null || thisPair.getValue1() == 0) {
                thisPair = createCategoricalNode(parent, e.getKey(), trainingData);
            }
            if (thisPair.getValue1() > bestScore) {
                bestScore = thisPair.getValue1();
                bestNode = thisPair.getValue0();
            }
        }

        // If we were unable to find a useful branch, return the leaf
        if (bestNode == null || bestScore < minimumScore)
            // Its a bad sign when this happens, normally something to debug
            return thisLeaf;

        double[] oldSplit = null;

        final LinkedList<? extends AbstractInstance> trueTrainingSet = Lists.newLinkedList(Iterables.filter(trainingData,
                bestNode.getInPredicate()));

        if (trueTrainingSet.size() < this.minLeafInstances) {
            return thisLeaf;
        }

        double trueWeight = 0;
        for (AbstractInstance instance : trueTrainingSet)
            trueWeight += instance.getWeight();

        final LinkedList<? extends AbstractInstance> falseTrainingSet = Lists.newLinkedList(Iterables.filter(trainingData,
                bestNode.getOutPredicate()));

        if (falseTrainingSet.size() < this.minLeafInstances) {
            return thisLeaf;
        }

        double falseWeight = 0;
        for (AbstractInstance instance : falseTrainingSet)
            falseWeight += instance.getWeight();

        if (trueWeight == 0 || falseWeight ==0) {
            return thisLeaf;
        }

        // We want to temporarily replace the split for an attribute for
        // descendants of an numeric branch, first the true split
        if (bestNode instanceof NumericBranch) {
            final NumericBranch ob = (NumericBranch) bestNode;
            oldSplit = splits.get(ob.attribute);
            splits.put(ob.attribute, createNumericSplit(trueTrainingSet, ob.attribute));
        }

        // Recurse down the true branch
        bestNode.trueChild = buildTree(bestNode, trueTrainingSet, depth + 1, splits);

        // And now replace the old split if this is an NumericBranch
        if (bestNode instanceof NumericBranch) {
            final NumericBranch ob = (NumericBranch) bestNode;
            splits.put(ob.attribute, createNumericSplit(falseTrainingSet, ob.attribute));
        }

        // Recurse down the false branch
        bestNode.falseChild = buildTree(bestNode, falseTrainingSet, depth + 1, splits);

        // And now replace the original split if this is an NumericBranch
        if (bestNode instanceof NumericBranch) {
            final NumericBranch ob = (NumericBranch) bestNode;
            splits.put(ob.attribute, oldSplit);
        }

        return bestNode;
    }

    private Map<String, AttributeCharacteristics> surveyTrainingData(final Iterable<? extends AbstractInstance> trainingData) {
    //    logger.debug("Surveying training data");
        //tells us if each attribute is numeric or not.
        Map<String, AttributeCharacteristics> attributeCharacteristics = Maps.newHashMap();

        for (AbstractInstance instance : trainingData) {
            for (Entry<String, Serializable> e : instance.getAttributes().entrySet()) {
                AttributeCharacteristics attributeCharacteristic = attributeCharacteristics.get(e.getKey());
                if (attributeCharacteristic == null) {
                    attributeCharacteristic = new AttributeCharacteristics();
                    attributeCharacteristics.put(e.getKey(), attributeCharacteristic);
                }
                if (!(e.getValue() instanceof Number)) {
                    attributeCharacteristic.isNumber = false;
                }
            }
        }
//        logger.debug("Survey complete");
        return attributeCharacteristics;
    }

    protected Pair<? extends Branch, Double> createCategoricalNode(Node parent, final String attribute,
                                                               final Iterable<? extends AbstractInstance> instances) {
      //  logger.debug("Creating categorical node for attribute {}", attribute);
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
                if (this.minCategoricalAttributeValueOccurances > 0) {
                    if (shouldWeIgnoreThisValue(testValCounts)) continue;
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
       // logger.debug("Created categorical node for attribute {}", attribute);
        return Pair.with(new CategoricalBranch(parent, attribute, bestSoFar), score);
    }

    private boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts) {
        double lowestClassificationCount = Double.MAX_VALUE;
        for (double classificationCount : testValCounts.getCounts().values()) {
            if (classificationCount < lowestClassificationCount) {
                lowestClassificationCount = classificationCount;
            }
        }
        return lowestClassificationCount < minCategoricalAttributeValueOccurances;
    }

    protected Pair<? extends Branch, Double> createNumericNode(Node parent, final String attribute,
                                                               final Iterable<? extends AbstractInstance> instances,
                                                               final double[] splits) {
//        logger.debug("Creating numeric node for attribute {}", attribute);

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
//        logger.debug("Created numeric node for attribute {}", attribute);
        return Pair.with(new NumericBranch(parent, attribute, bestThreshold), bestScore);
    }

    public static class AttributeCharacteristics {
        public boolean isNumber = true;
    }

}
