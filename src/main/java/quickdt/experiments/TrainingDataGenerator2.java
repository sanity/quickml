package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.data.*;
import quickdt.predictiveModels.randomForest.RandomForest;

import java.util.List;
import java.util.Random;



/**
 * Created by alexanderhawk on 1/16/14.
 */
public class TrainingDataGenerator2 {

    private Random uniformRandom;

    private int instances;
    private int numPredictiveAttributes;

    private double latentVariable; //a latent variable controls clickClassification
    private double decayConstantForNoClickEvent; //the higher the value, the less likely a No click event becomes.
    private double maxProbabilityOfClick;
    private String bidRequestAttributes[];


    public TrainingDataGenerator2(int instances, double maxProbabilityOfClick, String bidRequestAttributes[])  {
        this.uniformRandom = new Random();
        this.instances = instances;
        this.maxProbabilityOfClick = maxProbabilityOfClick;
        this.bidRequestAttributes = bidRequestAttributes;
        this.numPredictiveAttributes = bidRequestAttributes.length;
        initializeClickProbabilityDistribution();
    }

    public void getAverageDeviationInPredictedProbabilities(int samples, double onlyConsiderSamplesAboveThisProbability, RandomForest randomForest)  {
        Attributes attributes;
        double predictedProb;
        double rawPredictedProb;
        double actualClickProbability;
        double deviation = 0;

        for (int i = 0; i < samples; i++)  {
            attributes = getAttributesForAnInstance();
            actualClickProbability = getClickProbabilityFromLatentVariableValue();
            if (actualClickProbability > onlyConsiderSamplesAboveThisProbability)  {
                predictedProb = randomForest.getProbability(attributes, 1.0);
//                predictedProb  = randomForest.calibrator.correct(rawPredictedProb);
                deviation += Math.abs(actualClickProbability - predictedProb) / actualClickProbability;
                //    System.out.println("actualClickProbability : predictedProb : rawProb" + actualClickProbability + " : " + predictedProb + " " + rawPredictedProb);
            }
            else
                i--;
        }
        //       PrintStream treeView = new PrintStream(System.out);
        //      bidderPredictiveModel.clickPredictor.dump(treeView); //prints a sample tree for debugging purposes

        System.out.println("average deviation" + deviation/samples);
    }

    private Attributes getAttributesForAnInstance() {
        Attributes attributes =  new HashMapAttributes();

        double attributeValue;
        latentVariable = 0;
        String key;
        for (int j = 0; j < numPredictiveAttributes; j++)  {
            attributeValue = getAttributeValAndTheEffectOnTheLatentVariable(j);
            key = bidRequestAttributes[j]; // Integer.toString(j);
            attributes.put(key, attributeValue);
        }
        return attributes;
    }

    private double getClickProbabilityFromLatentVariableValue()  {
        return maxProbabilityOfClick * (1 - Math.exp(-decayConstantForNoClickEvent * latentVariable) );
    }

    private void initializeClickProbabilityDistribution() {
        double standardDeviationOfUniformVariableOn0to1 = Math.sqrt(1.0/12);
        double meanOfUniformVariableOn0to1 = 0.5;
        int stdsAboveTheMeanForRelevance = 16;  //make this number higher if you want more instances that have high click probabilities.
        double standardDeviationOfClassificationVariable = standardDeviationOfUniformVariableOn0to1 / Math.sqrt(numPredictiveAttributes);
        this.decayConstantForNoClickEvent = 1/(meanOfUniformVariableOn0to1 + stdsAboveTheMeanForRelevance*standardDeviationOfClassificationVariable);
        System.out.println("decay constant " + decayConstantForNoClickEvent );
        //System.exit(0);
    }

    public List<AbstractInstance> createTrainingData() {

        List<AbstractInstance> trainingData = Lists.<AbstractInstance>newArrayList();
        double attributeValue;
        AbstractInstance instance;
        Attributes attributes;
        double clickClassification;

        for (int i = 0; i < instances; i++)  {
            attributes = getAttributesForAnInstance();
            clickClassification = setClickValue();
            instance = new Instance(attributes, clickClassification);
            trainingData.add(instance);
        }
        return trainingData;
    }

    private double setClickValue() {
        double clickProbability = getClickProbabilityFromLatentVariableValue();
        double rand = uniformRandom.nextDouble();
        double clickClassification = rand < clickProbability ? 1.0 : 0.0;
        return clickClassification;
    }

    private double getAttributeValAndTheEffectOnTheLatentVariable(int attributeNumber) {
        double attributeVal = uniformRandom.nextDouble();
        latentVariable += attributeVal/numPredictiveAttributes;
        int attributeValForPredictiveModel = (int)(attributeVal*10000);
        return attributeValForPredictiveModel;
    }
}

