package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.data.*;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.PrintStream;
import java.util.List;
import java.util.Random;

/**
 * Created by alexanderhawk on 1/17/14.
 */
public class ProbDistOfVarDependentOnCatVars {

    private int instances;
    private int maxDepth;
    private int numTrees;
    private RandomForest randomForest;

    private Random attributeValueGenerator;
    private int numPredictiveAttributes;
    private int numAttributes;
    private int numAttributeVals;

    private Random classificationSampler;
    private int classification;
    private double classificationVar;
    private double stiffness;
    private double maxProbabilityOfPositiveClassification;

    private double probabilityRangesOfAttributeValue[][];
    private double impactOfAttributeValue[][];

    public ProbDistOfVarDependentOnCatVars(int instances, int maxDepth, int numTrees, int numNoiseAttributes, int numPredictiveAttributes, int numAttributeVals, double maxProbabilityOfPositiveClassification, double distanceAboveTheMeanForRelevance)  {

        initializeRandomForestProperties(instances, numTrees, maxDepth);
        initializeAttributeProperties(numPredictiveAttributes, numNoiseAttributes, numAttributeVals);
        initializeClassificationVariableProperties(maxProbabilityOfPositiveClassification, distanceAboveTheMeanForRelevance);

        List<Instance> trainingData = createTrainingData();
        this.randomForest = getRandomForest(trainingData);
    }

    public void getAverageDeviationInPredictedProbabilities(int samples, double onlyConsiderSamplesAboveThisProbability, boolean print)  {
        double attributeValue;
        Attributes attributes;
        double predictedProb;
        double actualProb;
        double deviation = 0;
        System.out.println("getting deviations\n");
        for (int sample = 0; sample < samples; sample++)  {
            attributes = new HashMapAttributes();
            classificationVar = 0;
            for (int attributeNumber = 0; attributeNumber < numAttributes; attributeNumber++)  {
                attributeValue = useAttribute(attributeNumber);
                attributes.put(Integer.toString(attributeNumber), attributeValue);
            }
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
        double instanceProbability = maxProbabilityOfPositiveClassification * (1 - Math.exp(-stiffness*classificationVar) );
        return instanceProbability;
    }
//        System.out.println("Probability of a click: " + randomForest.getProbability(HashMapAttributes.create("age", 11), "click"));

    private void initializeAttributeProperties(int numPredictiveAttributes, int numNoiseAttributes, int numAttributeVals) {
        this.attributeValueGenerator = new Random();
        this.numPredictiveAttributes = numPredictiveAttributes;
        this.numAttributes = numNoiseAttributes + numPredictiveAttributes;
        this.numAttributeVals = numAttributeVals;

        initializeProbabilityRangesOfAttributeValue();
        initializeImpactOfVariableValue();
    }

    private void initializeProbabilityRangesOfAttributeValue() {
        probabilityRangesOfAttributeValue = new double[numAttributes][numAttributeVals];
        double normalizationConst = 0;
        Random probabilityValueGenerator = new Random();
        for (int i=0; i<numAttributes; i++)  {
            normalizationConst = 0;
            for (int j=0; j<numAttributeVals; j++)  {
                probabilityRangesOfAttributeValue[i][j] = probabilityValueGenerator.nextDouble();
                normalizationConst +=  probabilityRangesOfAttributeValue[i][j];
            }
            for (int j=0; j<numAttributeVals; j++)  {
                probabilityRangesOfAttributeValue[i][j] /= normalizationConst;
                if ( j>0 )
                    probabilityRangesOfAttributeValue[i][j] += probabilityRangesOfAttributeValue[i][j-1];
        }
        }
    }

    private void initializeImpactOfVariableValue() {
        impactOfAttributeValue = new double[numAttributes][numAttributeVals];
        Random probabilityValueGenerator = new Random();
        for (int i=0; i<numPredictiveAttributes; i++)
            for (int j=0; j<numAttributeVals; j++)
                impactOfAttributeValue[i][j] = probabilityValueGenerator.nextDouble();//consider making a gaussian
    }


    private void initializeRandomForestProperties(int instances, int numTrees, int maxDepth) {
        this.instances = instances;
        this.numTrees = numTrees;
        this.maxDepth = maxDepth;
    }

    private void initializeClassificationVariableProperties(double maxProbabilityOfPositiveClassification, double distanceAboveTheMeanForRelevance) {
        this.classificationSampler = new Random();
        double meanOfPredictiveVariable = 0;
        for (int i=0; i<numPredictiveAttributes; i++)
            for (int j=0; j<numAttributeVals; j++)
                meanOfPredictiveVariable+= probabilityRangesOfAttributeValue[i][j]*impactOfAttributeValue[i][j];
        this.stiffness = 1/(distanceAboveTheMeanForRelevance*meanOfPredictiveVariable);
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
            for (int attributeNumber = 0; attributeNumber < numAttributes; attributeNumber++)  {
                attributeValue = useAttribute(attributeNumber);
                attributes.put(Integer.toString(attributeNumber), attributeValue);
            }

            classify();

            instance = new Instance(attributes, this.classification );
            trainingData.add(instance);
        }
        return trainingData;
    }

    private void classify() {
        double classProb = getInstanceProbability();
        double rand = classificationSampler.nextDouble();
        this.classification = rand < classProb ? 1 : 0;
    }

    private double useAttribute(int attributeNumber) {
        double attributeVal = sampleAttributeValueFromProbabilityDistribution(attributeNumber);
        incorporateAttributeInClassificationVar(attributeNumber, attributeVal);
        return attributeVal;
    }

    private double sampleAttributeValueFromProbabilityDistribution(int attributeNumber) {
        double attributeVal =0;
        double random = attributeValueGenerator.nextDouble();
        for (int i=0; i<numAttributeVals; i++)
            if (random < probabilityRangesOfAttributeValue[attributeNumber][i])  {
                attributeVal = impactOfAttributeValue[attributeNumber][i];
                break;
            }
        return attributeVal;
    }

    private void incorporateAttributeInClassificationVar(int attributeNumber, double attributeVal) {
        if (attributeNumber < numPredictiveAttributes)
            classificationVar += attributeVal;
    }

}
