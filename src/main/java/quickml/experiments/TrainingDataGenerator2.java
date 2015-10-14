package quickml.experiments;

import com.google.common.collect.Lists;
import quickml.data.*;
import quickml.data.instances.Instance;
import quickml.data.instances.InstanceImpl;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;

import java.io.Serializable;
import java.util.List;
import java.util.Random;



/**
 * TODO: This should probably be removed or replaced
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

    public void getAverageDeviationInPredictedProbabilities(int samples, double onlyConsiderSamplesAboveThisProbability, RandomDecisionForest randomDecisionForest)  {
        AttributesMap attributes;
        double predictedProb;
        double rawPredictedProb;
        double actualClickProbability;
        double deviation = 0;

        for (int i = 0; i < samples; i++)  {
            attributes = getAttributesForAnInstance();
            actualClickProbability = getClickProbabilityFromLatentVariableValue();
            if (actualClickProbability > onlyConsiderSamplesAboveThisProbability)  {
                predictedProb = randomDecisionForest.getProbability(attributes, 1.0);
//                predictedProb  = randomForest.calibrator.predict(rawPredictedProb);
                deviation += Math.abs(actualClickProbability - predictedProb) / actualClickProbability;
                //    System.out.println("actualClickProbability : predictedProb : rawProb" + actualClickProbability + " : " + predictedProb + " " + rawPredictedProb);
            }
            else
                i--;
        }
        //       PrintStream treeView = new PrintStream(System.out);
        //      bidderPredictiveModel.clickPredictor.dump(treeView); //prints a sample oldTree for debugging purposes

        System.out.println("average deviation" + deviation/samples);
    }

    private AttributesMap getAttributesForAnInstance() {
        AttributesMap attributes =  AttributesMap.newHashMap() ;

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

    public List<Instance<AttributesMap, Serializable>> createTrainingData() {

        List<Instance<AttributesMap, Serializable>> trainingData = Lists.newArrayList();
        Instance<AttributesMap, Serializable> instance;
        AttributesMap attributes;
        Double clickClassification;

        for (int i = 0; i < instances; i++)  {
            attributes = getAttributesForAnInstance();
            clickClassification = setClickValue();
            instance = new InstanceImpl<AttributesMap, Serializable>(attributes, clickClassification);
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

