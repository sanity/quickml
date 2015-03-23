package quickml.supervised.classifier.decisionTree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import org.javatuples.Pair;
import quickml.collections.MapUtils;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.decisionTree.tree.*;

import java.io.Serializable;
import java.util.*;

public final class TreeBuilder<T extends InstanceWithAttributesMap> implements PredictiveModelBuilder<Tree, T> {


    //TODO: belongs in the Branch builders in specific form
    private ForestConfigBuilder<T> configBuilder;
    private ForestConfig<T> forestConfig;
    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);

    public TreeBuilder(ForestConfigBuilder config) {
        this.configBuilder = config;
    }

    public TreeBuilder<T> copy() {
        return new TreeBuilder<>(configBuilder.copy());
    }

    public void updateBuilderConfig(Map<String, Object> cfg) {
        configBuilder.update(cfg);
    }

    @Override
    public Tree buildPredictiveModel(Iterable<T> trainingData) {
        List<T> trainingDataList = Utils.<T>iterableToList(trainingData);
        forestConfig = configBuilder.buildConfig(trainingDataList);
        //need to do tree construction here
        return buildTree(null, trainingDataList, 0);
    }
//all numeric and categorical node
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

    private Node buildTree(Branch parent, List<T> trainingData, final int depth) {
        Preconditions.checkArgument(!Iterables.isEmpty(trainingData), "At Depth: " + depth + ". Can't build a tree with no training data");
        if (depth >= config.maxDepth || trainingData.size() <= 2*minLeafInstances) {
            return getLeaf(parent, trainingData, depth);
        }

        Optional<? extends Branch> bestBranchOptional = findBestBranch(parent, trainingData);
        if (!bestBranchOptional.isPresent()) {
            return getLeaf(parent, trainingData, depth);
        }
        Branch bestBranch = bestBranchOptional.get();

        ArrayList<T> trueTrainingSet = Lists.newArrayList();
        ArrayList<T> falseTrainingSet = Lists.newArrayList();
        setTrueAndFalseTrainingSets(trainingData, bestBranch, trueTrainingSet, falseTrainingSet);

        trainingData = null;
        System.gc();//enable garbage collection

        bestBranch.trueChild = buildTree(bestBranch, trueTrainingSet, depth + 1);
        bestBranch.falseChild = buildTree(bestBranch, falseTrainingSet, depth + 1);

        return bestBranch;
    }

    private Optional<? extends Branch> findBestBranch(Branch parent, List<T> instances) {
        double bestScore = configBuilder.minScore;
        Optional<? extends Branch> bestBranchOptional = Optional.absent();
        Map<BranchType, BranchBuilder<T>> branchBuilders = getBranchBuilders();

        for (BranchType branchType : branchBuilders.keySet()) {
            BranchBuilder<T> branchBuilder =  branchBuilders.get(branchType);
            Optional<? extends Branch> thisBranchOptional = branchBuilder.findBestBranch(parent, instances);
            if (thisBranchOptional.isPresent()) {
                Branch thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {  //minScore evaluation delegated to branchBuilder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

    private Map<BranchType, BranchBuilder<T>> getBranchBuilders() {
        Map<BranchType, BranchBuilder<T>> branchBuilders = new HashMap<>();
        if (configBuilder.numericBranchBuilder!=null) {
            branchBuilders.put(BranchType.NUMERIC, configBuilder.numericBranchBuilder);
        }
        if (configBuilder.categoricalBranchBuilder !=null){
            branchBuilders.put(BranchType.CATEGORICAL, configBuilder.categoricalBranchBuilder);
        }
        if (configBuilder.booleanBranchBuilder !=null){
            branchBuilders.put(BranchType.BOOLEAN, configBuilder.booleanBranchBuilder);
        }
        return branchBuilders;
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
