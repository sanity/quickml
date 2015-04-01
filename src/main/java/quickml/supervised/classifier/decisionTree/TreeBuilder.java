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
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.tree.*;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public final class TreeBuilder<T extends ClassifierInstance> implements PredictiveModelBuilder<Tree, T> {

    public static final String MAX_DEPTH = "maxDepth";
    public static final String MIN_SCORE = "minScore";
    //the minimum number of times a categorical attribute value must be observed to be considered during splitting.
    //also the minimimum number of times a numeric attribute must be observed to fall inside a closed interval for that interval to be considered in a split decision
    public static final String MIN_OCCURRENCES_OF_ATTRIBUTE_VALUE = "minOccurrencesOfAttributeValue";
    public static final String MIN_LEAF_INSTANCES = "minLeafInstances";
    public static final String SCORER = "scorer";
    public static final String PENALIZE_CATEGORICAL_SPLITS = "penalizeCategoricalSplitsBySplitAttributeIntrinsicValue";
    public static final String ATTRIBUTE_IGNORING_STRATEGY = "attributeIgnoringStrategy";
    public static final String DEGREE_OF_GAIN_RATIO_PENALTY = "degreeOfGainRatioPenalty";
    public static final String IGNORE_ATTR_PROB = "ignoreAttributeAtNodeProbability";
    public static final String ORDINAL_TEST_SPLITS = "ordinalTestSpilts";
    public static final int SMALL_TRAINING_SET_LIMIT = 9;
    public static final int RESERVOIR_SIZE = 50;
    public static final Serializable MISSING_VALUE = "%missingVALUE%83257";
    private static final int HARD_MINIMUM_INSTANCES_PER_CATEGORICAL_VALUE = 10;

    private Scorer scorer;
    private int maxDepth = 5;
    private double minimumScore = 0.00000000000001;
    private int minDiscreteAttributeValueOccurances = 0;

    private int minLeafInstances = 0;

    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);
    private boolean penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = true;
    private double degreeOfGainRatioPenalty = 1.0;
    private int ordinalTestSpilts = 5;
    private double fractionOfDataToUseInHoldOutSet;

    private AttributeIgnoringStrategy attributeIgnoringStrategy = new IgnoreAttributesWithConstantProbability(0.0);

    //TODO: make it so only one thread computes the below 4 values since all trees compute the same values..
    private  Serializable minorityClassification;
    private  Serializable majorityClassification;
    private  double majorityToMinorityRatio = 1;
    private boolean binaryClassifications = true;
    Map<String, AttributeCharacteristics> attributeCharacteristics;


    public TreeBuilder() {
        this(new GiniImpurityScorer());
    }

    public TreeBuilder attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
        return this;
    }

    @Deprecated
    public TreeBuilder ignoreAttributeAtNodeProbability(double ignoreAttributeAtNodeProbability) {
        attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(ignoreAttributeAtNodeProbability));
        return this;
    }

    public TreeBuilder copy() {
        TreeBuilder<T> copy = new TreeBuilder<>();
        copy.scorer = scorer;
        copy.maxDepth = maxDepth;
        copy.minimumScore = minimumScore;
        copy.minDiscreteAttributeValueOccurances = minDiscreteAttributeValueOccurances;
        copy.minLeafInstances = minLeafInstances;
        copy.penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = penalizeCategoricalSplitsBySplitAttributeIntrinsicValue;
        copy.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        copy.ordinalTestSpilts = ordinalTestSpilts;
        copy.attributeIgnoringStrategy = attributeIgnoringStrategy.copy();
        copy.fractionOfDataToUseInHoldOutSet = fractionOfDataToUseInHoldOutSet;
        return copy;
    }

    public TreeBuilder(final Scorer scorer) {
        this.scorer = scorer;
    }

    public void updateBuilderConfig(final Map<String, Object> cfg) {
        if (cfg.containsKey(SCORER))
            scorer((Scorer) cfg.get(SCORER));
        if (cfg.containsKey(MAX_DEPTH))
            maxDepth((Integer) cfg.get(MAX_DEPTH));
        if (cfg.containsKey(MIN_SCORE))
            minimumScore((Double) cfg.get(MIN_SCORE));
        if (cfg.containsKey(MIN_OCCURRENCES_OF_ATTRIBUTE_VALUE))
            minCategoricalAttributeValueOccurances((Integer) cfg.get(MIN_OCCURRENCES_OF_ATTRIBUTE_VALUE));
        if (cfg.containsKey(MIN_LEAF_INSTANCES))
            minLeafInstances((Integer) cfg.get(MIN_LEAF_INSTANCES));
        if (cfg.containsKey(ORDINAL_TEST_SPLITS))
            ordinalTestSplits((Integer) cfg.get(ORDINAL_TEST_SPLITS));
        if (cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY))
            degreeOfGainRatioPenalty((Double) cfg.get(DEGREE_OF_GAIN_RATIO_PENALTY));
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY))
            attributeIgnoringStrategy((AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY));
        if (cfg.containsKey(IGNORE_ATTR_PROB))
            ignoreAttributeAtNodeProbability((Double)cfg.get(IGNORE_ATTR_PROB));

        penalizeCategoricalSplitsBySplitAttributeIntrinsicValue(cfg.containsKey(PENALIZE_CATEGORICAL_SPLITS) ? (Boolean) cfg.get(PENALIZE_CATEGORICAL_SPLITS) : true);
    }

    public TreeBuilder degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        return this;
    }

    public TreeBuilder ordinalTestSplits(int ordinalTestSpilts) {
        this.ordinalTestSpilts = ordinalTestSpilts;
        return this;
    }


    public TreeBuilder<T> scorer(final Scorer scorer) {
        this.scorer = scorer;
        return this;
    }


    public TreeBuilder<T> maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public TreeBuilder<T> binaryClassification(boolean binaryClassification) {
        this.binaryClassifications = binaryClassification;
        return this;
    }

    public TreeBuilder<T> minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public TreeBuilder<T> penalizeCategoricalSplitsBySplitAttributeIntrinsicValue(boolean useGainRatio) {
        this.penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = useGainRatio;
        return this;
    }

    public TreeBuilder<T> minCategoricalAttributeValueOccurances(int occurances) {
        this.minDiscreteAttributeValueOccurances = occurances;
        return this;
    }

    public TreeBuilder<T> minimumScore(double minimumScore) {
        this.minimumScore = minimumScore;
        return this;
    }

    @Override
    public Tree buildPredictiveModel(Iterable<T> trainingData) {
        List <T> trainingDataList = Lists.newArrayList();
        for (T instance : trainingData ) {
            trainingDataList.add(instance);
        }
        Set<Serializable> classifications = getClassificationProperties(trainingDataList);
        attributeCharacteristics = surveyTrainingData(trainingData);
        return new Tree(growTree(null, trainingDataList, 0), classifications);
    }

    private Set<Serializable> getClassificationProperties(Iterable<T> trainingData) {
        HashMap<Serializable, MutableInt> classificationsAndCounts = Maps.newHashMap();
        Serializable minorityClassification = null;
        Serializable majorityClassification = null;
        boolean binaryClassifications = true;
        double majorityToMinorityRatio = 1;

        for (T instance : trainingData) {
            Serializable classification = instance.getLabel();

                if (classificationsAndCounts.containsKey(classification)) {
                   classificationsAndCounts.get(classification).increment();

                } else
                    classificationsAndCounts.put(classification, new MutableInt(1));

        }
        if (classificationsAndCounts.size() > 2) {
            setBinaryClassifications(false);
            return new HashSet<>(classificationsAndCounts.keySet());
        }

        minorityClassification = null;
        double minorityClassificationCount = 0;

        majorityClassification = null;
        double majorityClassificationCount = 0;
        for (Serializable val : classificationsAndCounts.keySet()) {
            if (minorityClassification == null || classificationsAndCounts.get(val).doubleValue() < minorityClassificationCount) {
                minorityClassification = val;
                minorityClassificationCount = classificationsAndCounts.get(val).doubleValue();
            }
            if (majorityClassification == null || classificationsAndCounts.get(val).doubleValue() > majorityClassificationCount) {
                majorityClassification = val;
                majorityClassificationCount = classificationsAndCounts.get(val).doubleValue();
            }
        }
        majorityToMinorityRatio = classificationsAndCounts.get(majorityClassification).doubleValue()
                / classificationsAndCounts.get(minorityClassification).doubleValue();

        writeClassificationPropertiesOfDataSet(minorityClassification, majorityClassification, binaryClassifications, majorityToMinorityRatio);
        return new HashSet<>(classificationsAndCounts.keySet());
    }

    private void setBinaryClassifications(boolean binaryClassifications) {
        this.binaryClassifications = binaryClassifications;
    }

    private void writeClassificationPropertiesOfDataSet(Serializable minorityClassification, Serializable majorityClassification, boolean binaryClassifications, double majorityToMinorityRatio) {
        this.minorityClassification = minorityClassification;
        this.majorityClassification = majorityClassification;
        this.binaryClassifications = binaryClassifications;
        this.majorityToMinorityRatio = majorityToMinorityRatio;
    }

    private double[] createNumericSplit(final List<T> trainingData, final String attribute) {
        int numSamples = Math.min(RESERVOIR_SIZE, trainingData.size());
        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(numSamples, rand);
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / RESERVOIR_SIZE);
        for (int i=0; i<trainingData.size(); i+=samplesToSkipPerStep) {
            Serializable value = trainingData.get(i).getAttributes().get(attribute);
            if (value == null) {
                continue;
            }
            reservoirSampler.sample(((Number) value).doubleValue());
        }

        return getSplit(reservoirSampler);
    }
    private Map<String, double[]> createNumericSplits(final List<T> trainingData) {
        final Map<String, ReservoirSampler<Double>> rsm = Maps.newHashMap();
        int numSamples = Math.min(RESERVOIR_SIZE, trainingData.size());
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / RESERVOIR_SIZE);

        for (int i=0; i<numSamples; i+=samplesToSkipPerStep) {
            for (final Entry<String, Serializable> attributeEntry : trainingData.get(i).getAttributes().entrySet()) {
                if (attributeEntry.getValue() instanceof Number) {
                    ReservoirSampler<Double> reservoirSampler = rsm.get(attributeEntry.getKey());
                    if (reservoirSampler == null) {
                        reservoirSampler = new ReservoirSampler<>(numSamples, rand);
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

        final double[] split = new double[ordinalTestSpilts - 1];
        final int indexMultiplier = splitList.size() / (split.length + 1);//num elements / num bins
        for (int x = 0; x < split.length; x++) {
            split[x] = splitList.get((x + 1) * indexMultiplier);
        }
        return split;
    }

    private Node growTree(Branch parent,  List<T> trainingData, final int depth) {
        Preconditions.checkArgument(!Iterables.isEmpty(trainingData), "At Depth: " + depth + ". Can't build a tree with no training data");
        if (depth >= maxDepth) {
            return getLeaf(parent, trainingData, depth);
        }

        Pair<? extends Branch, Double> bestPair = getBestNodePair(parent, trainingData);
        Branch bestNode = bestPair != null ? bestPair.getValue0() : null;
        double bestScore = bestPair != null ? bestPair.getValue1() : 0;

        if (bestNode == null || bestScore < minimumScore) {
            //bestNode will be null if no attribute could provide a split that had enough statistically significant variable values
            // to produce 2 children where each had at least minInstancesPerLeafSamples.
            //The best score condition naturally catches the situation where all instances have the same classification.
            return getLeaf(parent, trainingData, depth);
        }


        ArrayList<T> trueTrainingSet = Lists.newArrayList();
        ArrayList<T> falseTrainingSet = Lists.newArrayList();
        setTrueAndFalseTrainingSets(trainingData, bestNode, trueTrainingSet, falseTrainingSet);

        bestNode.trueChild = growTree(bestNode, trueTrainingSet, depth + 1);
        bestNode.falseChild = growTree(bestNode, falseTrainingSet, depth + 1);

        return bestNode;
    }

    private Leaf getLeaf(Node parent, List<T> trainingData, int depth) {
        return new Leaf(parent, trainingData, depth);
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

    private Pair<? extends Branch, Double> getBestNodePair(Branch parent, List<T> trainingData) {
        //should not be doing the following operation every time we call growTree

        boolean smallTrainingSet = isSmallTrainingSet(trainingData);
        Pair<? extends Branch, Double> bestPair = null;
        //TODO: make this lazy in the sense that only numeric attributes that are not randomly rignored should have this done
        for (final Entry<String, AttributeCharacteristics> attributeCharacteristicsEntry : attributeCharacteristics.entrySet()) {
            if (this.attributeIgnoringStrategy.ignoreAttribute(attributeCharacteristicsEntry.getKey(), parent)) {
                continue;
            }

            Pair<? extends Branch, Double> thisPair = null;
            Pair<? extends Branch, Double> numericPair = null;
            Pair<? extends Branch, Double> categoricalPair = null;

            if (!smallTrainingSet && attributeCharacteristicsEntry.getValue().isNumber) {
                numericPair = createNumericBranch(parent, attributeCharacteristicsEntry.getKey(), trainingData);
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
        double intrinsicValueOfAttribute = getIntrinsicValueOfAttribute(valuesWithClassificationCounters, numTrainingExamples);

        for (final AttributeValueWithClassificationCounter valueWithClassificationCounter : valuesWithClassificationCounters) {
            final ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;
            if (testValCounts == null || valueWithClassificationCounter.attributeValue.equals(MISSING_VALUE)) { // Also a kludge, figure out why
                continue;
            }
            if (this.minDiscreteAttributeValueOccurances > 0) {
                if (!testValCounts.hasSufficientData()) continue;
            }
            inCounts = inCounts.add(testValCounts);
            outCounts = outCounts.subtract(testValCounts);

            if (inCounts.getTotal() < minLeafInstances || outCounts.getTotal() < minLeafInstances) {
                continue;
            }

            double thisScore = scorer.scoreSplit(inCounts, outCounts);
            valuesInTheInset++;
            if (penalizeCategoricalSplitsBySplitAttributeIntrinsicValue) {
                thisScore = thisScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (thisScore / intrinsicValueOfAttribute);            }

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
        if (bestScore==0)
            return null;
        else {

            Pair<CategoricalBranch, Double> bestPair = Pair.with(new CategoricalBranch(parent, attribute, inSet, probabilityOfBeingInInset), bestScore);
            return bestPair;
        }
    }

    private int labelAttributeValuesWithInsufficientData(List<AttributeValueWithClassificationCounter> valuesWithClassificationCounters) {
        int attributesWithSuffValues = 0;
        for (final AttributeValueWithClassificationCounter valueWithClassificationCounter : valuesWithClassificationCounters) {
            if (this.minDiscreteAttributeValueOccurances > 0) {
                ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;
                if (attributeValueOrIntervalOfValuesHasInsufficientStatistics(testValCounts)) {
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

    private double getIntrinsicValueOfAttribute(List<AttributeValueWithClassificationCounter> valuesWithCCs, double numTrainingExamples) {
        double informationValue = 0;
        double attributeValProb = 0;

        for (AttributeValueWithClassificationCounter attributeValueWithClassificationCounter : valuesWithCCs) {
            ClassificationCounter classificationCounter = attributeValueWithClassificationCounter.classificationCounter;
            attributeValProb = classificationCounter.getTotal() / (numTrainingExamples);//-insufficientDataInstances);
            informationValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
        }

        return informationValue;
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
                if (this.minDiscreteAttributeValueOccurances > 0) {
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
        final boolean notEnoughTrainingDataGivenNumberOfValues = averageInstancesPerValue < Math.max(this.minDiscreteAttributeValueOccurances,
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

    private boolean attributeValueOrIntervalOfValuesHasInsufficientStatistics(final ClassificationCounter testValCounts) {
        Preconditions.checkArgument(majorityClassification!=null && minorityClassification !=null);
        Map<Serializable, Double> counts = testValCounts.getCounts();
        if (counts.containsKey(minorityClassification) &&
                counts.get(minorityClassification) > minDiscreteAttributeValueOccurances) {
            return false;
        }

        if (counts.containsKey(majorityClassification) &&
                counts.get(majorityClassification) > majorityToMinorityRatio * minDiscreteAttributeValueOccurances) {
            return false;
        }

        if (hasBothMinorityAndMajorityClassifications(counts)
                && hasSufficientStatisticsForBothClassifications(counts)) {
            return false;
        }

        return true;
    }
    private boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts) {
        Map<Serializable, Double> counts = testValCounts.getCounts();

        for (Serializable key : counts.keySet()) {
            if (counts.get(key).doubleValue() < minDiscreteAttributeValueOccurances) {
                return true;
            }
        }

        return false;
    }

    private boolean hasSufficientStatisticsForBothClassifications(Map<Serializable, Double> counts) {
        return counts.get(majorityClassification) > 0.6 * majorityToMinorityRatio * minDiscreteAttributeValueOccurances
                && counts.get(minorityClassification) > 0.6 * minDiscreteAttributeValueOccurances;
    }

    private boolean hasBothMinorityAndMajorityClassifications(Map<Serializable, Double> counts) {
        return counts.containsKey(majorityClassification) && counts.containsKey(minorityClassification);
    }

    private Pair<? extends Branch, Double> createNumericBranch(Node parent, final String attribute,
                                                               List<T> instances) {

        double bestScore = 0;
        double bestThreshold = 0;

        double lastThreshold = Double.MIN_VALUE;
        double probabilityOfBeingInInset = 0;

        final double[] splits = createNumericSplit(instances, attribute);
        for (final double threshold : splits) {

            if (threshold == lastThreshold) {
                continue;
            }
            lastThreshold = threshold;

            Iterable<T> inSet = Iterables.filter(instances, new GreaterThanThresholdPredicate(attribute, threshold));
            Iterable<T> outSet = Iterables.filter(instances, new LessThanEqualThresholdPredicate(attribute, threshold));
            ClassificationCounter inClassificationCounts = ClassificationCounter.countAll(inSet);
            ClassificationCounter outClassificationCounts = ClassificationCounter.countAll(outSet);

            if (binaryClassifications) {
                if (attributeValueOrIntervalOfValuesHasInsufficientStatistics(inClassificationCounts)
                        || inClassificationCounts.getTotal() < minLeafInstances
                        || attributeValueOrIntervalOfValuesHasInsufficientStatistics(outClassificationCounts)
                        || outClassificationCounts.getTotal() < minLeafInstances) {
                    continue;
                }
            } else if (shouldWeIgnoreThisValue(inClassificationCounts) || shouldWeIgnoreThisValue(outClassificationCounts)) {
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
