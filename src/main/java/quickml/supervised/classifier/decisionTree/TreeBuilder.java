package quickml.supervised.classifier.decisionTree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import org.javatuples.Pair;
import quickml.collections.MapUtils;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.tree.*;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public final class TreeBuilder<T extends InstanceWithAttributesMap> implements PredictiveModelBuilder<Tree, T> {

    public static final String MAX_DEPTH = "maxDepth";
    public static final String BEST_BRANCH_FINDER = "bestBranchFinder";
    public static final String MIN_SCORE = "minScore";
    public static final String MIN_LEAF_INSTANCES = "minLeafInstances";


    //the minimum number of times a categorical attribute value must be observed to be considered during splitting.
    //also the minimimum number of times a numeric attribute must be observed to fall inside a closed interval for that interval to be considered in a split decision
    public static final String MIN_OCCURRENCES_OF_ATTRIBUTE_VALUE = "minOccurrencesOfAttributeValue";
    public static final String SCORER = "scorer";
    public static final String PENALIZE_CATEGORICAL_SPLITS = "penalizeCategoricalSplitsBySplitAttributeIntrinsicValue";
    public static final String ATTRIBUTE_IGNORING_STRATEGY = "attributeIgnoringStrategy";
    public static final String DEGREE_OF_GAIN_RATIO_PENALTY = "degreeOfGainRatioPenalty";
    public static final String ORDINAL_TEST_SPLITS = "ordinalTestSpilts";
    public static final String NUM_SAMPLES_FOR_COMPUTING_NUMERIC_SPLIT_POINTS = "numSamplesForComputingNumericSplitPoints";
    public static final int SMALL_TRAINING_SET_LIMIT = 9;
    public static final Serializable MISSING_VALUE = "%missingVALUE%83257";
    private static final int SAMPLES_PER_BIN = 10;
    private static final int HARD_MINIMUM_INSTANCES_PER_CATEGORICAL_VALUE = 2;
    //TODO: make it so only one thread computes the below 4 values since all trees compute the same values..
    ClassificationProperties classificationProperties;
    //TODO: belongs in the Branch builders in specific form
    AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private BestBranchFinder bestBranchFinder;
    private int numSamplesForComputingNumericSplitPoints = 50;
    private Scorer scorer;
    private int maxDepth = 5;
    private double minimumScore = 0.00000000000001;
    private int minOccurancesOfAttributeValue = 0;
    private int minLeafInstances = 0;
    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);
    private boolean penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = true;
    private double degreeOfGainRatioPenalty = 1.0;
    private int ordinalTestSpilts = 5;
    private double fractionOfDataToUseInHoldOutSet;
    private AttributeIgnoringStrategy attributeIgnoringStrategy = new IgnoreAttributesWithConstantProbability(0.0);

    public TreeBuilder() {
        this(new GiniImpurityScorer());
    }

    public TreeBuilder(final Scorer scorer) {
        this.scorer = scorer;
    }

    public TreeBuilder attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
        return this;
    }

    public TreeBuilder bestBranchFinder(BestBranchFinder bestBranchFinder) {
        this.bestBranchFinder = bestBranchFinder;
        return this;
    }

    public TreeBuilder numSamplesForComputingNumericSplitPoints(int numSamplesForComputingNumericSplitPoints) {
        /**
         * set this field to the size of the training set to ensure trial numeric split points are chosen deterministically
         */
        this.numSamplesForComputingNumericSplitPoints = numSamplesForComputingNumericSplitPoints;
        return this;
    }

    public TreeBuilder copy() {
        TreeBuilder<T> copy = new TreeBuilder<>();
        copy.bestBranchFinder = bestBranchFinder.copy();
        copy.scorer = scorer;
        copy.maxDepth = maxDepth;
        copy.minimumScore = minimumScore;
        copy.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
        copy.minLeafInstances = minLeafInstances;
        copy.penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = penalizeCategoricalSplitsBySplitAttributeIntrinsicValue;
        copy.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        copy.ordinalTestSpilts = ordinalTestSpilts;
        copy.attributeIgnoringStrategy = attributeIgnoringStrategy.copy();
        copy.fractionOfDataToUseInHoldOutSet = fractionOfDataToUseInHoldOutSet;
        copy.numSamplesForComputingNumericSplitPoints = numSamplesForComputingNumericSplitPoints;
        return copy;
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

        penalizeCategoricalSplitsBySplitAttributeIntrinsicValue(cfg.containsKey(PENALIZE_CATEGORICAL_SPLITS) ? (Boolean) cfg.get(PENALIZE_CATEGORICAL_SPLITS) : true);
    }

    public TreeBuilder degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        return this;
    }

    public TreeBuilder ordinalTestSplits(int ordinalTestSpilts) {
        this.ordinalTestSpilts = ordinalTestSpilts;
        this.numSamplesForComputingNumericSplitPoints = SAMPLES_PER_BIN * ordinalTestSpilts;
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

    public TreeBuilder<T> minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public TreeBuilder<T> penalizeCategoricalSplitsBySplitAttributeIntrinsicValue(boolean useGainRatio) {
        this.penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = useGainRatio;
        return this;
    }

    public TreeBuilder<T> minCategoricalAttributeValueOccurances(int occurances) {
        this.minOccurancesOfAttributeValue = occurances;
        return this;
    }

    public TreeBuilder<T> minimumScore(double minimumScore) {
        this.minimumScore = minimumScore;
        return this;
    }

    @Override
    public Tree buildPredictiveModel(Iterable<T> trainingData) {
        List<T> trainingDataList = iterableToList(trainingData);
        classificationProperties = ClassificationProperties.<T>getClassificationProperties(trainingDataList);  //should only exist in ClassificationTree
        if (classificationProperties.classificationsAreBinary()) {
            attributeValueIgnoringStrategy = new BinaryClassAttributeValueIgnoringStrategy((BinaryClassificationProperties) classificationProperties, minOccurancesOfAttributeValue);
        } else {
            attributeValueIgnoringStrategy = new MultiClassAtributeIgnoringStrategy(minOccurancesOfAttributeValue);
        }
        attributeCharacteristics = TrainingDataSurveyor.<T>groupAttributesByType(trainingDataList);

        return new Tree(buildTree(null, trainingDataList, 0), classificationProperties.getClassifications());
    }

    private List<T> iterableToList(Iterable<T> trainingData) {
        List<T> trainingDataList = Lists.newArrayList();
        for (T instance : trainingData) {
            trainingDataList.add(instance);
        }
        return trainingDataList;
    }


    private double[] createNumericSplit(final List<T> trainingData, final String attribute) {
        int numSamples = Math.min(numSamplesForComputingNumericSplitPoints, trainingData.size());
        if (numSamples == trainingData.size()) {
            return getDeterministicSplit(trainingData, attribute); //makes code testable, because now can be made deterministic by making numSamplesForComputingNumericSplitPoints < trainingData.size.
        }

        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(numSamples, rand);
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / numSamplesForComputingNumericSplitPoints);
        if (trainingData.size() / numSamplesForComputingNumericSplitPoints == 1) {
            samplesToSkipPerStep = 2;
        }
        for (int i = 0; i < trainingData.size(); i += samplesToSkipPerStep) {
            Serializable value = trainingData.get(i).getAttributes().get(attribute);
            if (value == null) {
                continue;
            }
            reservoirSampler.sample(((Number) value).doubleValue());
        }

        return getSplit(reservoirSampler);
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

    private double[] getDeterministicSplit(List<T> instances, String attribute) {

        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final T sample : instances) {
            splitList.add(((Number) (sample.getAttributes().get(attribute))).doubleValue());
        }
        if (splitList.isEmpty()) {
            throw new RuntimeException("Split list empty");
        }
        Collections.sort(splitList);

        final double[] split = new double[ordinalTestSpilts - 1];
        final int indexMultiplier = splitList.size() / (split.length + 1);//num elements / num bins
        for (int x = 0; x < split.length && (x + 1) * indexMultiplier < splitList.size(); x++) {
            split[x] = splitList.get((x + 1) * indexMultiplier);
        }
        return split;
    }

    private Node buildTree(Branch parent, final List<T> trainingData, final int depth) {
        Preconditions.checkArgument(!Iterables.isEmpty(trainingData), "At Depth: " + depth + ". Can't build a tree with no training data");
        if (depth >= maxDepth || trainingData.size() <= 2*minLeafInstances) {
            return getLeaf(parent, trainingData, depth);
        }

        Optional<? extends Branch> bestBranchOptional = bestBranchFinder.findBestBranch(parent, trainingData);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, trainingData, depth);
        }
        Branch bestBranch = bestBranchOptional.get();
        ArrayList<T> trueTrainingSet = Lists.newArrayList();
        ArrayList<T> falseTrainingSet = Lists.newArrayList();
        setTrueAndFalseTrainingSets(trainingData, bestBranch, trueTrainingSet, falseTrainingSet);

        bestBranch.trueChild = buildTree(bestBranch, trueTrainingSet, depth + 1);
        bestBranch.falseChild = buildTree(bestBranch, falseTrainingSet, depth + 1);

        return bestBranch;
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



    private Pair<? extends Branch, Double> createCategoricalNode(Node parent, String attribute, Iterable<T> instances) {
        if (classificationProperties.classificationsAreBinary()) {
            return createTwoClassCategoricalNode(parent, attribute, instances);
        } else {
            return createNClassCategoricalNode(parent, attribute, instances);
        }
    }

    private Pair<? extends Branch, Double> createTwoClassCategoricalNode(Node parent, final String attribute, final Iterable<T> instances) {
        BinaryClassificationProperties binaryClassificationProperties = (BinaryClassificationProperties) classificationProperties;
        double bestScore = 0;
        final Pair<ClassificationCounter, List<AttributeValueWithClassificationCounter>> valueOutcomeCountsPairs =
                ClassificationCounter.getSortedListOfAttributeValuesWithClassificationCounters(instances, attribute, binaryClassificationProperties.minorityClassification);  //returs a list of ClassificationCounterList

        ClassificationCounter outCounts = new ClassificationCounter(valueOutcomeCountsPairs.getValue0()); //classification counter treating all values the same
        ClassificationCounter inCounts = new ClassificationCounter(); //the histogram of counts by classification for the in-set

        final List<AttributeValueWithClassificationCounter> valuesWithClassificationCounters = valueOutcomeCountsPairs.getValue1(); //map of value _> classificationCounter
        double numTrainingExamples = valueOutcomeCountsPairs.getValue0().getTotal();

        Serializable lastValOfInset = valuesWithClassificationCounters.get(0).attributeValue;
        double probabilityOfBeingInInset = 0;
        int valuesInTheInset = 0;
        int attributesWithSufficientValues = labelAttributeValuesWithInsufficientData(binaryClassificationProperties, valuesWithClassificationCounters);
        if (attributesWithSufficientValues <= 1)
            return null; //there is just 1 value available.
        double intrinsicValueOfAttribute = getIntrinsicValueOfAttribute(valuesWithClassificationCounters, numTrainingExamples);

        for (final AttributeValueWithClassificationCounter valueWithClassificationCounter : valuesWithClassificationCounters) {
            final ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;
            if (testValCounts == null || valueWithClassificationCounter.attributeValue.equals(MISSING_VALUE)) { // Also a kludge, figure out why
                continue;
            }
            if (this.minOccurancesOfAttributeValue > 0) {
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
                thisScore = thisScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (thisScore / intrinsicValueOfAttribute);
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
        if (bestScore == 0)
            return null;
        else {

            Pair<CategoricalBranch, Double> bestPair = Pair.with(new CategoricalBranch(parent, attribute, inSet, probabilityOfBeingInInset), bestScore);
            return bestPair;
        }
    }

    private int labelAttributeValuesWithInsufficientData(BinaryClassificationProperties bcp, List<AttributeValueWithClassificationCounter> valuesWithClassificationCounters) {
        int attributesWithSuffValues = 0;
        for (final AttributeValueWithClassificationCounter valueWithClassificationCounter : valuesWithClassificationCounters) {
            if (this.minOccurancesOfAttributeValue > 0) {
                ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;
                if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(testValCounts)) {
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

        final Set<Serializable> values = getAttributeValues(instances, attribute);

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
                if (this.minOccurancesOfAttributeValue > 0) {
                    if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(testValCounts)) continue;
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
        final boolean notEnoughTrainingDataGivenNumberOfValues = averageInstancesPerValue < Math.max(this.minOccurancesOfAttributeValue,
                HARD_MINIMUM_INSTANCES_PER_CATEGORICAL_VALUE);
        if (notEnoughTrainingDataGivenNumberOfValues) {
            return true;
        }
        return false;
    }

    private Set<Serializable> getAttributeValues(final Iterable<T> trainingData, final String attribute) {
        final Set<Serializable> values = Sets.newHashSet();
        for (T instance : trainingData) {
            Serializable value = instance.getAttributes().get(attribute);
            if (value == null) value = MISSING_VALUE;
            values.add(value);
        }
        return values;
    }

    private Pair<? extends Branch, Double> createNumericBranch(Node parent, final String attribute,
                                                               List<T> instances) {

        double bestScore = 0;
        double bestThreshold = 0;
        double probabilityOfBeingInInset = 0;

        final double[] splits = createNumericSplit(instances, attribute);
        List<ClassificationCountingPair> classificationCounts = Lists.newArrayList();
        for (int i = 0; i < splits.length; i++) {
            classificationCounts.add(new ClassificationCountingPair(new HashMap<Serializable, Long>(), new HashMap<Serializable, Long>()));
        }
        for (T instance : instances) {

            double attributeVal = ((Number) (instance.getAttributes().get(attribute))).doubleValue();
            double threshold = 0, previousThreshold = 0;

            for (int i = 0; i < splits.length; i++) {
                previousThreshold = threshold;
                threshold = splits[i];
                if (previousThreshold == threshold && i != 0)
                    continue;

                ClassificationCountingPair classificationCountingPair = classificationCounts.get(i);
                if (attributeVal > threshold) {
                    updateCounts(instance.getLabel(), classificationCountingPair.inCounter);
                } else {
                    updateCounts(instance.getLabel(), classificationCountingPair.outCounter);
                }
            }
        }

        for (int i = 0; i < splits.length; i++) {

            double threshold = splits[i];
            ClassificationCounter inClassificationCounts = new ClassificationCounter(classificationCounts.get(i).inCounter);
            ClassificationCounter outClassificationCounts = new ClassificationCounter(classificationCounts.get(i).outCounter);

            if (classificationProperties.classificationsAreBinary()) {

                if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(inClassificationCounts)
                        || inClassificationCounts.getTotal() < minLeafInstances
                        || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(outClassificationCounts)
                        || outClassificationCounts.getTotal() < minLeafInstances) {
                    continue;
                }
            } else if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(inClassificationCounts) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(outClassificationCounts)) {
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


    private void updateCounts(Serializable label, HashMap<Serializable, Long> mapOfCounts) {
        long previousCounts = mapOfCounts.containsKey(label) ? mapOfCounts.get(label) : 0L;
        mapOfCounts.put(label, previousCounts + 1L);
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
