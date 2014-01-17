package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.Attributes;
import quickdt.HashMapAttributes;
import quickdt.Instance;
import quickdt.TreeBuilder;
import quickdt.randomForest.RandomForest;
import quickdt.randomForest.RandomForestBuilder;

import java.io.PrintStream;
import java.util.List;
import java.util.Random;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class ProbDistOfSumOfWeigtedRandVars {

    private int instances;
    private int maxDepth;
    private int numTrees;
    private RandomForest randomForest;

    private Random attributeValueGenerator;
    private int numPredictiveAttributes;
    private int numAttributes;

    private Random classificationSampler;
    private int classification;
    private double classificationVar;
    private double stiffness;
    private double maxProbabilityOfPositiveClassification;

    public ProbDistOfSumOfWeigtedRandVars(int instances, int maxDepth, int numTrees, int numNoiseAttributes, int numPredictiveAttributes, double maxProbabilityOfPositiveClassification, int stdsAboveTheMeanForRelevance)  {

        initializeRandomForestProperties(instances, numTrees, maxDepth);
        initializeAttributeProperties(numPredictiveAttributes, numNoiseAttributes);
        initializeClassificationVariableProperties(maxProbabilityOfPositiveClassification, stdsAboveTheMeanForRelevance);

        List<Instance> trainingData = createTrainingData();
        this.randomForest = getRandomForest(trainingData);
    }

    public void getAverageDeviationInPredictedProbabilities(int samples, double onlyConsiderSamplesAboveThisProbability, boolean print)  {
        double attributeValue;
        Attributes attributes;
        double predictedProb;
        double actualProb;
        double deviation = 0;

        for (int sample = 0; sample < samples; sample++)  {
            attributes = new HashMapAttributes();
            for (int attributeNumber = 1; attributeNumber <= numAttributes; attributeNumber++)  {
                attributeValue = useAttribute(attributeNumber);
                attributes.put(Integer.toString(attributeNumber), attributeValue);
            }
            normalizeClassificationVar();
            actualProb = getInstanceProbability();
            if (actualProb > onlyConsiderSamplesAboveThisProbability)  {
                predictedProb = randomForest.getProbability(attributes, 1);
                deviation += Math.abs(actualProb - predictedProb) / actualProb;
                System.out.println("actualProb : predictedProb " + actualProb + " : " + predictedProb);
            }
            else
                sample--;
            }
//        Writer writer = new PrintWriter(System.out);
        PrintStream treeView = new PrintStream(System.out); //new WriterOutputStream(writer));
        randomForest.dump(treeView);

        System.out.println("average deviation" + deviation/samples);
        return;
    }

    private double getInstanceProbability()  {
            return maxProbabilityOfPositiveClassification * (1 - Math.exp(-stiffness*classificationVar) );
    }
//        System.out.println("Probability of a click: " + randomForest.getProbability(HashMapAttributes.create("age", 11), "click"));

    private void initializeAttributeProperties(int numPredictiveAttributes, int numNoiseAttributes) {
        this.attributeValueGenerator = new Random();
        this.numPredictiveAttributes = numPredictiveAttributes;
        this.numAttributes = numNoiseAttributes + numPredictiveAttributes;
    }

    private void initializeRandomForestProperties(int instances, int numTrees, int maxDepth) {
        this.instances = instances;
        this.numTrees = numTrees;
        this.maxDepth = maxDepth;
    }

    private void initializeClassificationVariableProperties(double maxProbabilityOfPositiveClassification,int stdsAboveTheMeanForRelevance) {
        this.classificationSampler = new Random();
        double standardDeviationOfUniformVariableOn0to1 = Math.sqrt(1.0/12);
        double meanOfUniformVariableOn0to1 = 0.5;
        double standardDeviationOfClassificationVariable = standardDeviationOfUniformVariableOn0to1 / Math.sqrt(numPredictiveAttributes);
        this.stiffness = 1/(meanOfUniformVariableOn0to1 + stdsAboveTheMeanForRelevance*standardDeviationOfClassificationVariable);
        this.maxProbabilityOfPositiveClassification = maxProbabilityOfPositiveClassification;
    }

    private RandomForest getRandomForest(List<Instance> trainingData) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }

    private  List<Instance> createTrainingData() {
        List<Instance> trainingData = Lists.newArrayList();
        double attributeValue;
        Instance instance;
        Attributes attributes;

        for (int i = 0; i < instances; i++)  {
            attributes = new HashMapAttributes();
            classificationVar = 0;
            for (int attributeNumber = 1; attributeNumber <= numAttributes; attributeNumber++)  {
                attributeValue = useAttribute(attributeNumber);
                attributes.put(Integer.toString(attributeNumber), attributeValue);
            }

            normalizeClassificationVar();
            classify();

            instance = new Instance(attributes, this.classification );
            trainingData.add(instance);
        }
        return trainingData;
    }

    private void normalizeClassificationVar() {
        classificationVar /= numPredictiveAttributes;
    }

    private void classify() {
        double classProb = getInstanceProbability();
        double rand = classificationSampler.nextDouble();
        this.classification = rand < classProb ? 1 : 0;
    }

    private double useAttribute(int attributeNumber) {
        double attributeVal;
        if (attributeNumber <= numPredictiveAttributes)  {
            attributeVal = getPredictiveAttribute(attributeNumber);
        }
        else
            attributeVal = attributeValueGenerator.nextDouble();
        return attributeVal;
    }

    private double getPredictiveAttribute(int attributeNumber) {
        double attributeVal;
        attributeVal = (attributeValueGenerator.nextDouble())/attributeNumber + (1-1/attributeNumber);
        incorporateAttributeInClassificationVar(attributeNumber, attributeVal);
        return attributeVal;
    }

    private void incorporateAttributeInClassificationVar(int attributeNumber, double attributeVal) {
        if (attributeNumber <= numPredictiveAttributes)
            classificationVar += attributeVal;
    }

}
