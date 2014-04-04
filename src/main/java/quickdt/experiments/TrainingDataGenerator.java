package quickdt.experiments;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class TrainingDataGenerator {
/*
    private Random uniformRandom;

    private int instances;
    private int numPredictiveAttributes;

    private double latentVariable; //a latent variable controls clickClassification
    private double decayConstantForNoClickEvent; //the higher the value, the less likely a No click event becomes.
    private double maxProbabilityOfClick;
    private String bidRequestAttributes[];


    public TrainingDataGenerator(int instances, double maxProbabilityOfClick, String bidRequestAttributes[])  {
        this.uniformRandom = new Random();
        this.instances = instances;
        this.maxProbabilityOfClick = maxProbabilityOfClick;
        this.bidRequestAttributes = bidRequestAttributes;
        this.numPredictiveAttributes = bidRequestAttributes.length;
        initializeClickProbabilityDistribution();
    }

    public void getAverageDeviationInPredictedProbabilities(int samples, double onlyConsiderSamplesAboveThisProbability, BidderPredictiveModel bidderPredictiveModel)  {
        Attributes attributes;
        double predictedProb;
        double rawPredictedProb;
        double actualClickProbability;
        double deviation = 0;

        for (int i = 0; i < samples; i++)  {
            attributes = getAttributesForAnInstance();
            actualClickProbability = getClickProbabilityFromLatentVariableValue();
            if (actualClickProbability > onlyConsiderSamplesAboveThisProbability)  {
                rawPredictedProb = bidderPredictiveModel.getProbability(attributes, 1);
                predictedProb  = bidderPredictiveModel.calibrator.correct(rawPredictedProb);
                deviation += Math.abs(actualClickProbability - predictedProb) / actualClickProbability;
    //            System.out.println("actualClickProbability : predictedProb " + actualClickProbability + " : " + predictedProb);
            }
            else
                i--;
            }
        PrintStream treeView = new PrintStream(System.out);
        randomForest.dump(treeView); //prints a sample tree for debugging purposes

        System.out.println("average deviation" + deviation/samples);
        return;
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
    }

    public  List<Instance> createTrainingData() {

        List<Instance> trainingData = Lists.newArrayList();
        double attributeValue;
        Instance instance;
        Attributes attributes;
        int clickClassification;

        for (int i = 0; i < instances; i++)  {
            attributes = getAttributesForAnInstance();
            clickClassification = setClickValue();
            instance = new Instance(attributes, clickClassification);
            trainingData.add(instance);
        }
        return trainingData;
    }

    private int setClickValue() {
        double clickProbability = getClickProbabilityFromLatentVariableValue();
        double rand = uniformRandom.nextDouble();
        int clickClassification = rand < clickProbability ? 1 : 0;
        return clickClassification;
    }

    private double getAttributeValAndTheEffectOnTheLatentVariable(int attributeNumber) {
        double attributeVal = uniformRandom.nextDouble();
        latentVariable += attributeVal/numPredictiveAttributes;
        int attributeValForPredictiveModel = (int)(attributeVal*10000);
        return attributeValForPredictiveModel;
    }
    */
}
