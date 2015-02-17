package quickml.supervised.classifier.decisionTree;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import org.apache.commons.lang.mutable.MutableInt;
import org.javatuples.Pair;
import quickml.collections.MapUtils;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.tree.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public final class TreeBuilder<T extends ClassifierInstance> implements PredictiveModelBuilder<Tree, T> {

    public static final String IGNORE_ATTR_PROB = "ignoreAttrProb";
    public static final String MAX_DEPTH = "maxDepth";
    public static final String MIN_SCORE = "minScore";
    public static final String MIN_CAT_ATTR_OCC = "minCatAttrOcc";
    public static final String MIN_LEAF_INSTANCES = "minLeafInstances";
    public static final String SCORER = "scorer";
    public static final String PENALIZE_CATEGORICAL_SPLITS = "penalizeCategoricalSplitsBySplitAttributeInformationValue";


    public static final int ORDINAL_TEST_SPLITS = 5;
    public static final int SMALL_TRAINING_SET_LIMIT = 9;
    public static final int RESERVOIR_SIZE = 1000;
    public static final Serializable MISSING_VALUE = "%missingVALUE%83257";
    private static final int HARD_MINIMUM_INSTANCES_PER_CATEGORICAL_VALUE = 10;

    private Scorer scorer;
    private int maxDepth = Integer.MAX_VALUE;
    private double ignoreAttributeAtNodeProbability = 0.0;
    private double minimumScore = 0.00000000000001;
    private int minCategoricalAttributeValueOccurances = 0;
    private int minLeafInstances = 0;
    private boolean binaryClassifications = true;
    private Serializable minorityClassification;
    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);
    private boolean penalizeCategoricalSplitsBySplitAttributeInformationValue = true;

    public TreeBuilder() {
        this(new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE));
    }

    public void updateBuilderConfig(final Map<String, Object> cfg) {
        if (cfg.containsKey(SCORER))
            scorer((Scorer) cfg.get(SCORER));
        if (cfg.containsKey(IGNORE_ATTR_PROB))
            ignoreAttributeAtNodeProbability((Double) cfg.get(IGNORE_ATTR_PROB));
        if (cfg.containsKey(MAX_DEPTH))
            maxDepth((Integer) cfg.get(MAX_DEPTH));
        if (cfg.containsKey(MIN_SCORE))
            minimumScore((Double) cfg.get(MIN_SCORE));
        if (cfg.containsKey(MIN_CAT_ATTR_OCC))
            minCategoricalAttributeValueOccurances((Integer) cfg.get(MIN_CAT_ATTR_OCC));
        if (cfg.containsKey(MIN_LEAF_INSTANCES))
            minLeafInstances((Integer) cfg.get(MIN_LEAF_INSTANCES));
        penalizeCategoricalSplitsBySplitAttributeInformationValue(cfg.containsKey(PENALIZE_CATEGORICAL_SPLITS) ? (Boolean) cfg.get(PENALIZE_CATEGORICAL_SPLITS) : true);
    }

    public TreeBuilder(final Scorer scorer) {
        this.scorer = scorer;
    }

    public TreeBuilder scorer(final Scorer scorer) {
        this.scorer = scorer;
        return this;
    }


    public TreeBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public TreeBuilder binaryClassification(boolean binaryClassification) {
        this.binaryClassifications = binaryClassification;
        return this;
    }

    public TreeBuilder minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public TreeBuilder penalizeCategoricalSplitsBySplitAttributeInformationValue(boolean useGainRatio) {
        this.penalizeCategoricalSplitsBySplitAttributeInformationValue = useGainRatio;
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
    public Tree buildPredictiveModel(Iterable<T> trainingData) {
        Set<Serializable> classifications = getClassificationProperties(trainingData);
        return new Tree(buildTree(null, trainingData, 0, createNumericSplits(trainingData)), classifications);
    }

    private Set<Serializable> getClassificationProperties(Iterable<T> trainingData) {
        HashMap<Serializable, MutableInt> classifications = Maps.newHashMap();
        for (T instance : trainingData) {
            Serializable classification = instance.getLabel();
            if (classifications.containsKey(classification)) {
                classifications.get(classification).increment();
            } else
                classifications.put(classification, new MutableInt(1));

            if (classifications.size() > 2) {
                binaryClassifications = false;
                return new HashSet<>(classifications.keySet());
            }
        }

        minorityClassification = null;
        double minorityClassificationCount = 0;
        for (Serializable val : classifications.keySet())
            if (minorityClassification == null || classifications.get(val).doubleValue() < minorityClassificationCount) {
                minorityClassification = val;
                minorityClassificationCount = classifications.get(val).doubleValue();
            }
        return new HashSet<>(classifications.keySet());
    }

    private double[] createNumericSplit(final Iterable<T> trainingData, final String attribute) {
        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(RESERVOIR_SIZE, rand);
        for (final T instance : trainingData) {
            Serializable value = instance.getAttributes().get(attribute);
            if (value == null) value = 0;
            reservoirSampler.sample(((Number) value).doubleValue());
        }

        return getSplit(reservoirSampler);
    }

    private Map<String, double[]> createNumericSplits(final Iterable<T> trainingData) {
        final Map<String, ReservoirSampler<Double>> rsm = Maps.newHashMap();
        for (final T instance : trainingData) {
            for (final Entry<String, Serializable> attributeEntry : instance.getAttributes().entrySet()) {
                if (attributeEntry.getValue() instanceof Number) {
                    ReservoirSampler<Double> reservoirSampler = rsm.get(attributeEntry.getKey());
                    if (reservoirSampler == null) {
                        reservoirSampler = new ReservoirSampler<>(RESERVOIR_SIZE, rand);
                        rsm.put(attributeEntry.getKey(), reservoirSampler);
                    }
                    reservoirSampler.sample(((Number) attributeEntry.getValue()).doubleValue());
                }
            }
        }

        final Map<String, double[]> splits = Maps.newHashMap();

        for (final Entry<String, ReservoirSampler<Double>> e : rsm.entrySet()) {
            final double[] split = getSplit(e.getValue());
            splits.put(e.getKey(), split);
        }
        return splits;
    }

    private double[] getSplit(ReservoirSampler<Double> reservoirSampler) {
        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final Double sample : reservoirSampler.getSamples()) {
            splitList.add(sample);
        }
        if (splitList.isEmpty()) {
            throw new RuntimeException("Split list empty");
        }
        Collections.sort(splitList);

        final double[] split = new double[ORDINAL_TEST_SPLITS - 1];
        final int indexMultiplier = splitList.size() / (split.length + 1);//num elements / num bins
        for (int x = 0; x < split.length; x++) {
            split[x] = splitList.get((x + 1) * indexMultiplier);
        }
        return split;
    }

    private Node buildTree(Node parent, final Iterable<T> trainingData, final int depth,
                           final Map<String, double[]> splits) {
        Preconditions.checkArgument(!Iterables.isEmpty(trainingData), "At Depth: " + depth + ". Can't build a tree with no training data");
        final Leaf thisLeaf = new Leaf(parent, trainingData, depth);

        if (depth >= maxDepth) {
            return thisLeaf;
        }

        Pair<? extends Branch, Double> bestPair = getBestNodePair(parent, trainingData, splits);
        Branch bestNode = bestPair != null ? bestPair.getValue0() : null;
        double bestScore = bestPair != null ? bestPair.getValue1() : 0;

        // If we were unable to find a useful branch, return the leaf
        if (bestNode == null || bestScore < minimumScore) {
            // will be null if all attributes are ignored, and best score will be 0 if
            //1 of 3 things happen: (1) all instances in the node have the same classification, (2) each attribute tried has just 1 observed value
            //(3) subsets with the same attribute value have the same distribution of classifications
            return thisLeaf;
        }

        ArrayList<T> trueTrainingSet = Lists.newArrayList();
        ArrayList<T> falseTrainingSet = Lists.newArrayList();
        setTrueAndFalseTrainingSets(trainingData, bestNode, trueTrainingSet, falseTrainingSet);


        if (trueTrainingSet.size() < this.minLeafInstances) {
            return thisLeaf;
        }

        if (falseTrainingSet.size() < this.minLeafInstances) {
            return thisLeaf;
        }

        double trueWeight = getTotalWeight(trueTrainingSet);
        double falseWeight = getTotalWeight(falseTrainingSet);
        if (trueWeight == 0 || falseWeight == 0) {
            return thisLeaf;
        }

        double[] oldSplit = null;
        // We want to temporarily replace the split for an attribute for
        // descendants of an numeric branch, first the true split
        if (bestNode instanceof NumericBranch) {
            final NumericBranch bestBranch = (NumericBranch) bestNode;
            oldSplit = splits.get(bestBranch.attribute);
            splits.put(bestBranch.attribute, createNumericSplit(trueTrainingSet, bestBranch.attribute));
        }

        // Recurse down the true branch
        bestNode.trueChild = buildTree(bestNode, trueTrainingSet, depth + 1, splits);

        // And now replace the old split if this is an NumericBranch
        if (bestNode instanceof NumericBranch) {
            final NumericBranch bestBranch = (NumericBranch) bestNode;
            splits.put(bestBranch.attribute, createNumericSplit(falseTrainingSet, bestBranch.attribute));
        }

        // Recurse down the false branch
        bestNode.falseChild = buildTree(bestNode, falseTrainingSet, depth + 1, splits);

        // And now replace the original split if this is an NumericBranch
        if (bestNode instanceof NumericBranch) {
            final NumericBranch bestBranch = (NumericBranch) bestNode;
            splits.put(bestBranch.attribute, oldSplit);
        }
        return bestNode;
    }

    private void setTrueAndFalseTrainingSets(Iterable<T> trainingData, Branch bestNode, List<T> trueTrainingSet, List<T> falseTrainingSet) {
        //put instances with attribute values into appropriate training sets
        for (T instance : trainingData) {
            if (bestNode.decide(instance.getAttributes())) {
                trueTrainingSet.add(instance);
            } else {
                falseTrainingSet.add(instance);
            }
        }
    }

    private Pair<? extends Branch, Double> getBestNodePair(Node parent, Iterable<T> trainingData, final Map<String, double[]> splits) {
        //should not be doing the following operation every time we call buildTree
        Map<String, AttributeCharacteristics> attributeCharacteristics = surveyTrainingData(trainingData);

        boolean smallTrainingSet = isSmallTrainingSet(trainingData);
        Pair<? extends Branch, Double> bestPair = null;
        //TODO: make this lazy in the sense that only numeric attributes that are not randomly rignored should have this done
        for (final Entry<String, AttributeCharacteristics> attributeCharacteristicsEntry : attributeCharacteristics.entrySet()) {
            if (this.ignoreAttributeAtNodeProbability > 0 && MapUtils.random.nextDouble() < this.ignoreAttributeAtNodeProbability) {// || attributeCharacteristicsEntry.getKey().equals(splitAttribute)) {
                continue;
            }

            Pair<? extends Branch, Double> thisPair = null;
            Pair<? extends Branch, Double> numericPair = null;
            Pair<? extends Branch, Double> categoricalPair = null;

            if (!smallTrainingSet && attributeCharacteristicsEntry.getValue().isNumber) {
                numericPair = createNumericNode(parent, attributeCharacteristicsEntry.getKey(), trainingData, splits.get(attributeCharacteristicsEntry.getKey()));
            } else if (!attributeCharacteristicsEntry.getValue().isNumber) {
                categoricalPair = createCategoricalNode(parent, attributeCharacteristicsEntry.getKey(), trainingData);
            }

            if (numericPair != null) {
                thisPair = numericPair;
            } else {
                thisPair = categoricalPair;//(numericPair.getValue1() > categoricalPair.getValue1()) ? numericPair : categoricalPair;
            }
            if (bestPair == null || (thisPair != null && bestPair != null && thisPair.getValue1() > bestPair.getValue1())) {
                bestPair = thisPair;
            }
        }
        return bestPair;
    }

    private double getTotalWeight(List<T> trainingSet) {
        double trueWeight = 0;
        for (T instance : trainingSet) {
            trueWeight += instance.getWeight();
        }
        return trueWeight;
    }

    private boolean isSmallTrainingSet(Iterable<T> trainingData) {
        boolean smallTrainingSet = true;
        int tsCount = 0;
        for (T instance : trainingData) {
            tsCount++;
            if (tsCount > SMALL_TRAINING_SET_LIMIT) {
                smallTrainingSet = false;
                break;
            }
        }
        return smallTrainingSet;
    }

    private Map<String, AttributeCharacteristics> surveyTrainingData(final Iterable<T> trainingData) {
        //tells us if each attribute is numeric or not.
        Map<String, AttributeCharacteristics> attributeCharacteristics = Maps.newHashMap();

        for (T instance : trainingData) {
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
        return attributeCharacteristics;
    }

    private Pair<? extends Branch, Double> createCategoricalNode(Node parent, String attribute, Iterable<T> instances) {
        if (binaryClassifications) {
            return createTwoClassCategoricalNode(parent, attribute, instances);
        } else {
            return createNClassCategoricalNode(parent, attribute, instances);
        }
    }

    private Pair<? extends Branch, Double> createTwoClassCategoricalNode(Node parent, final String attribute, final Iterable<T> instances) {

        double bestScore = 0;
        final Pair<ClassificationCounter, List<AttributeValueWithClassificationCounter>> valueOutcomeCountsPairs =
                ClassificationCounter.getSortedListOfAttributeValuesWithClassificationCounters(instances, attribute, minorityClassification);  //returs a list of ClassificationCounterList

        ClassificationCounter outCounts = new ClassificationCounter(valueOutcomeCountsPairs.getValue0()); //classification counter treating all values the same
        ClassificationCounter inCounts = new ClassificationCounter(); //the histogram of counts by classification for the in-set

        final List<AttributeValueWithClassificationCounter> valuesWithClassificationCounters = valueOutcomeCountsPairs.getValue1(); //map of value _> classificationCounter
        double numTrainingExamples = valueOutcomeCountsPairs.getValue0().getTotal();

        Serializable lastValOfInset = valuesWithClassificationCounters.get(0).attributeValue;
        double probabilityOfBeingInInset = 0;
        int valuesInTheInset = 0;
        int attributesWithSufficientValues = labelAttributeValuesWithInsufficientData(valuesWithClassificationCounters);
        if (attributesWithSufficientValues <= 1)
            return null; //there is just 1 value available.
        double informationValue = getInformationValueOfAttribute(valuesWithClassificationCounters, numTrainingExamples);

        for (final AttributeValueWithClassificationCounter valueWithClassificationCounter : valuesWithClassificationCounters) {
            final ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;
            if (testValCounts == null || valueWithClassificationCounter.attributeValue.equals(MISSING_VALUE)) { // Also a kludge, figure out why
                continue;
            }
            if (this.minCategoricalAttributeValueOccurances > 0) {
                if (!testValCounts.hasSufficientData()) continue;
            }
            inCounts = inCounts.add(testValCounts);
            outCounts = outCounts.subtract(testValCounts);

            if (inCounts.getTotal() < minLeafInstances || outCounts.getTotal() < minLeafInstances) {
                continue;
            }

            double thisScore = scorer.scoreSplit(inCounts, outCounts);
            valuesInTheInset++;
            if (penalizeCategoricalSplitsBySplitAttributeInformationValue) {
                thisScore /= informationValue;
            }

            if (thisScore > bestScore) {
                bestScore = thisScore;
                lastValOfInset = valueWithClassificationCounter.attributeValue;
                probabilityOfBeingInInset = inCounts.getTotal() / (inCounts.getTotal() + outCounts.getTotal());
            }
        }
        final Set<Serializable> inSet = Sets.newHashSet();
        final Set<Serializable> outSet = Sets.newHashSet();
        boolean insetIsBuiltNowBuildingOutset = false;
        inCounts = new ClassificationCounter();
        outCounts = new ClassificationCounter();

        for (AttributeValueWithClassificationCounter attributeValueWithClassificationCounter : valuesWithClassificationCounters) {
            if (!insetIsBuiltNowBuildingOutset && attributeValueWithClassificationCounter.classificationCounter.hasSufficientData()) {
                inSet.add(attributeValueWithClassificationCounter.attributeValue);
                inCounts.add(attributeValueWithClassificationCounter.classificationCounter);
                if (attributeValueWithClassificationCounter.attributeValue.equals(lastValOfInset)) {
                    insetIsBuiltNowBuildingOutset = true;
                }
            } else {
                outCounts.add(attributeValueWithClassificationCounter.classificationCounter);

                //outSet.add(attributeValueWithClassificationCounter.attributeValue);
            }
        }

        Pair<CategoricalBranch, Double> bestPair = Pair.with(new CategoricalBranch(parent, attribute, inSet, probabilityOfBeingInInset), bestScore);
        return bestPair;
    }

    private int labelAttributeValuesWithInsufficientData(List<AttributeValueWithClassificationCounter> valuesWithClassificationCounters) {
        int attributesWithSuffValues = 0;
        for (final AttributeValueWithClassificationCounter valueWithClassificationCounter : valuesWithClassificationCounters) {
            if (this.minCategoricalAttributeValueOccurances > 0) {
                ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;
                if (shouldWeIgnoreThisValue(testValCounts)) {
                    testValCounts.setHasSufficientData(false);
                } else {
                    attributesWithSuffValues++;
                }
            } else {
                attributesWithSuffValues++;
            }
        }

        return attributesWithSuffValues;
    }

    private double getInformationValueOfAttribute(List<AttributeValueWithClassificationCounter> valuesWithCCs, double numTrainingExamples) {
        double informationValue = 0;
        double attributeValProb = 0;

        for (AttributeValueWithClassificationCounter attributeValueWithClassificationCounter : valuesWithCCs) {
            ClassificationCounter classificationCounter = attributeValueWithClassificationCounter.classificationCounter;
            attributeValProb = classificationCounter.getTotal() / (numTrainingExamples);//-insufficientDataInstances);
            informationValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
        }

        return informationValue;
    }

    private double getInformationValueOfNumericAttribute(int numberOfBins) {
        return 1;//-Math.log(1/numberOfBins)/Math.log(2);
    }

    private Pair<? extends Branch, Double> createNClassCategoricalNode(Node parent, final String attribute,
                                                                       final Iterable<T> instances) {

        final Set<Serializable> values = getAttrinbuteValues(instances, attribute);

        if (insufficientTrainingDataGivenNumberOfAttributeValues(instances, values)) return null;

        final Set<Serializable> inValueSet = Sets.newHashSet(); //the in-set

        ClassificationCounter inSetClassificationCounts = new ClassificationCounter(); //the histogram of counts by classification for the in-set

        final Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> valueOutcomeCountsPair = ClassificationCounter
                .countAllByAttributeValues(instances, attribute);
        ClassificationCounter outSetClassificationCounts = valueOutcomeCountsPair.getValue0(); //classification counter treating all values the same

        final Map<Serializable, ClassificationCounter> valueOutcomeCounts = valueOutcomeCountsPair.getValue1(); //map of value _> classificationCounter
        double insetScore = 0;
        while (true) {
            com.google.common.base.Optional<ScoreValuePair> bestValueAndScore = com.google.common.base.Optional.absent();
            //values should be greater than 1
            for (final Serializable thisValue : values) {
                final ClassificationCounter testValCounts = valueOutcomeCounts.get(thisValue);
                //TODO: the next 3 lines may no longer be needed. Verify.
                if (testValCounts == null || thisValue == null || thisValue.equals(MISSING_VALUE)) {
                    continue;
                }
                if (this.minCategoricalAttributeValueOccurances > 0) {
                    if (shouldWeIgnoreThisValue(testValCounts)) continue;
                }
                final ClassificationCounter testInCounts = inSetClassificationCounts.add(testValCounts);
                final ClassificationCounter testOutCounts = outSetClassificationCounts.subtract(testValCounts);

                double scoreWithThisValueAddedToInset = scorer.scoreSplit(testInCounts, testOutCounts);

                if (!bestValueAndScore.isPresent() || scoreWithThisValueAddedToInset > bestValueAndScore.get().getScore()) {
                    bestValueAndScore = com.google.common.base.Optional.of(new ScoreValuePair(scoreWithThisValueAddedToInset, thisValue));
                }
            }

            if (bestValueAndScore.isPresent() && bestValueAndScore.get().getScore() > insetScore) {
                insetScore = bestValueAndScore.get().getScore();
                final Serializable bestValue = bestValueAndScore.get().getValue();
                inValueSet.add(bestValue);
                values.remove(bestValue);
                final ClassificationCounter bestValOutcomeCounts = valueOutcomeCounts.get(bestValue);
                inSetClassificationCounts = inSetClassificationCounts.add(bestValOutcomeCounts);
                outSetClassificationCounts = outSetClassificationCounts.subtract(bestValOutcomeCounts);

            } else {
                break;
            }
        }
        if (inSetClassificationCounts.getTotal() < minLeafInstances || outSetClassificationCounts.getTotal() < minLeafInstances) {
            return null;
        }
        //because inSetClassificationCounts is only mutated to better insets during the for loop...it corresponds to the actual inset here.
        double probabilityOfBeingInInset = inSetClassificationCounts.getTotal() / (inSetClassificationCounts.getTotal() + outSetClassificationCounts.getTotal());
        return Pair.with(new CategoricalBranch(parent, attribute, inValueSet, probabilityOfBeingInInset), insetScore);
    }

    private boolean insufficientTrainingDataGivenNumberOfAttributeValues(final Iterable<T> trainingData, final Set<Serializable> values) {
        final int averageInstancesPerValue = Iterables.size(trainingData) / values.size();
        final boolean notEnoughTrainingDataGivenNumberOfValues = averageInstancesPerValue < Math.max(this.minCategoricalAttributeValueOccurances,
                HARD_MINIMUM_INSTANCES_PER_CATEGORICAL_VALUE);
        if (notEnoughTrainingDataGivenNumberOfValues) {
            return true;
        }
        return false;
    }

    private Set<Serializable> getAttrinbuteValues(final Iterable<T> trainingData, final String attribute) {
        final Set<Serializable> values = Sets.newHashSet();
        for (T instance : trainingData) {
            Serializable value = instance.getAttributes().get(attribute);
            if (value == null) value = MISSING_VALUE;
            values.add(value);
        }
        return values;
    }

    private boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts) {
        Map<Serializable, Double> counts = testValCounts.getCounts();
        if (counts.size() == 1)
            if (testValCounts.getTotal() < 2 * minCategoricalAttributeValueOccurances)
                return true;
        for (Serializable key : counts.keySet()) {
            if (counts.get(key).doubleValue() < minCategoricalAttributeValueOccurances)
                return true;
        }
        return false;
    }

    private Pair<? extends Branch, Double> createNumericNode(Node parent, final String attribute,
                                                             Iterable<T> instances,
                                                             final double[] splits) {
        double bestScore = 0;
        double bestThreshold = 0;

        double lastThreshold = Double.MIN_VALUE;
        double probabilityOfBeingInInset = 0;
        for (final double threshold : splits) {
            // Sometimes we can get a few thresholds the same, avoid wasted
            // effort when we do
            if (threshold == lastThreshold) {
                continue;
            }
            lastThreshold = threshold;

            Iterable<T> inSet = Iterables.filter(instances, new GreaterThanThresholdPredicate(attribute, threshold));
            Iterable<T> outSet = Iterables.filter(instances, new LessThanEqualThresholdPredicate(attribute, threshold));
            ClassificationCounter inClassificationCounts = ClassificationCounter.countAll(inSet);
            ClassificationCounter outClassificationCounts = ClassificationCounter.countAll(outSet);

            //here, we treat bins as categorical attributes, and therefore require a minimum number of samples be had for both the inset and outset.  this minimum number is
            //somewhat arbitrarily sets to 4*minCategoricalAttributeValueOccurances
            if (inClassificationCounts.getTotal() < minLeafInstances || inClassificationCounts.getTotal() < 4 * minCategoricalAttributeValueOccurances
                    || outClassificationCounts.getTotal() < minLeafInstances || outClassificationCounts.getTotal() < 4 * minCategoricalAttributeValueOccurances) {
                continue;
            }

            double thisScore = scorer.scoreSplit(inClassificationCounts, outClassificationCounts);
            if (thisScore > bestScore) {
                bestScore = thisScore;
                bestThreshold = threshold;
                probabilityOfBeingInInset = inClassificationCounts.getTotal() / (inClassificationCounts.getTotal() + outClassificationCounts.getTotal());
            }
        }
        if (bestScore == 0) {
            return null;
        }
        return Pair.with(new NumericBranch(parent, attribute, bestThreshold, probabilityOfBeingInInset), bestScore);
    }

    public static class AttributeCharacteristics {
        public boolean isNumber = true;
    }

    private class GreaterThanThresholdPredicate implements Predicate<T> {

        private final String attribute;
        private final double threshold;

        public GreaterThanThresholdPredicate(String attribute, double threshold) {
            this.attribute = attribute;
            this.threshold = threshold;
        }

        @Override
        public boolean apply(@Nullable T input) {
            try {
                if (input == null) {//consider deleting
                    return false;
                }
                Serializable value = input.getAttributes().get(attribute);
                if (value == null) {
                    value = 0;
                }
                return ((Number) value).doubleValue() > threshold;
            } catch (final ClassCastException e) { // Kludge, need to
                // handle better
                return false;
            }
        }
    }

    private class LessThanEqualThresholdPredicate implements Predicate<T> {

        private final String attribute;
        private final double threshold;

        public LessThanEqualThresholdPredicate(String attribute, double threshold) {
            this.attribute = attribute;
            this.threshold = threshold;
        }

        @Override
        public boolean apply(@Nullable T input) {
            try {
                if (input == null) {
                    return false;
                }
                Serializable value = input.getAttributes().get(attribute);
                if (value == null) {
                    value = Double.MIN_VALUE;
                }
                return ((Number) value).doubleValue() <= threshold; //missing values should go the way of the outset.  Future improvement shoud allow missing values to go way of either inset or outset
            } catch (final ClassCastException e) { // Kludge, need to
                // handle better
                return false;
            }
        }
    }

    private class ScoreValuePair {
        private double score;
        private Serializable value;

        private ScoreValuePair(final double score, final Serializable value) {
            this.score = score;
            this.value = value;
        }

        public double getScore() {
            return score;
        }

        public Serializable getValue() {
            return value;
        }
    }

}
