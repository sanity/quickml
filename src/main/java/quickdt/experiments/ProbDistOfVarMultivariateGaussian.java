package quickdt.experiments;

//import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;

/**
 * Created by alexanderhawk on 1/21/14.
 */
public class ProbDistOfVarMultivariateGaussian {
/*
    private int instances;
    private int maxDepth;
    private int numTrees;
    private RandomForest randomForest;

    private Random attributeValueGenerator;
    private int numPredictiveAttributes;
    private int numAttributes;
    private int numVariables;
    private int numAttributeVals;
    private int variableNumberOfResponse;
    private double degreeOfCovariance;

    private Random probabilityOfMakingAMonteCarloMove;
    private double monteCarloMoveProbability;
    private int numRawResponseStates;
    private double stdsAboveTheMeanForPositiveResponse;

    MultivariateNormalDistribution variableDistribution;
    private double currentSample[];
    private double proposedSample[];
    private int globalCount = 0;
    //private String []

    public ProbDistOfVarMultivariateGaussian(int instances, int maxDepth, int numTrees, int numNoiseAttributes, int numPredictiveAttributes, int numAttributeVals, int numRawResponseStates, double stdsAboveTheMeanForPositiveResponse, int stepsBeforeMixingSetsIn, double degreeOfCovariance, double monteCarloMoveProbability)  {
        initializeRandomForestProperties(instances, numTrees, maxDepth);
        initializeAttributeProperties(numPredictiveAttributes, numNoiseAttributes, numAttributeVals, degreeOfCovariance, numRawResponseStates, stdsAboveTheMeanForPositiveResponse, monteCarloMoveProbability);
        initializeMultiVariateNormalDistribution();
        initializeCurrentSample(stepsBeforeMixingSetsIn);
        List<Instance> trainingData = createTrainingData();
        this.randomForest = getRandomForest(trainingData);
    }

    public void getAverageDeviationInPredictedProbabilities(int samples, double onlyConsiderSamplesAboveThisProbability, boolean print)  {
        Instance instance;
        double predictedProb;
        double actualProb;
        double deviation = 0;
        System.out.println("getting deviations\n");
        for (double val : currentSample) System.out.println("val: " + val);

        System.out.printf("sample repsonse0 %f %n", currentSample[currentSample.length - 1]);

        for (int sample = 0; sample < samples; sample++)  {
            instance = sampleInstance("sampleResponseTrue");
            System.out.flush();
            //System.out.printf("sample repsonse %f %n", currentSample[currentSample.length - 1]);
            for (double val : currentSample) System.out.println("val: " + val);
            actualProb = getActualConditionalProbabilityOfClick();
            if (actualProb > onlyConsiderSamplesAboveThisProbability)  {
                predictedProb = randomForest.getProbability(instance.getAttributes(), 1.0);
                deviation += Math.abs(actualProb - predictedProb) / actualProb;
             //   if (print)
             //       System.out.println("actualProb : predictedProb " + actualProb + " : " + predictedProb);
             //       for (double val : currentSample) System.out.println("val: " + val);
            }
            else
                sample--;
        }
        PrintStream treeView = new PrintStream(System.out); //new WriterOutputStream(writer));
        randomForest.dump(treeView);

        System.out.println("average deviation" + deviation/samples);
        return;
    }

    private double getActualConditionalProbabilityOfClick() {
      currentSample[variableNumberOfResponse] = numRawResponseStates/2.0 - 0.5;
      double jointProbabilityForObservingAttributesWithAClick = variableDistribution.density(currentSample);
      double marginalProbabilityForObservingTheAttributes = getMarginalProbabilityForObservingAttributes(currentSample);
      double conditionalProbabilityOfAClick = jointProbabilityForObservingAttributesWithAClick / marginalProbabilityForObservingTheAttributes;
      System.out.println("margProb " + marginalProbabilityForObservingTheAttributes + " : jointProb " + jointProbabilityForObservingAttributesWithAClick + " conProb : " + conditionalProbabilityOfAClick + "\n" );

      return  conditionalProbabilityOfAClick;
    }

    private double getMarginalProbabilityForObservingAttributes(double sample[]) {
        double marginalProbabilityForObservingAttributes = 0;
        double minRawResponse = -numRawResponseStates/2.0 + 0.5;
        for (int i = 0; i < numRawResponseStates; i++)  {
          sample[variableNumberOfResponse] = minRawResponse + i;
          marginalProbabilityForObservingAttributes += variableDistribution.density(sample);
        }
        return marginalProbabilityForObservingAttributes;
    }

    private void initializeCurrentSample(int stepsBeforeMixingSetsIn) {
        Random initialAttributeVal = new Random();
        currentSample = new double[numVariables];
        proposedSample = new double [numVariables];
        int stateNumber;
        for (int i = 0; i < numVariables; i++) {
            if (i<numAttributes)  {
                stateNumber = initialAttributeVal.nextInt(numAttributeVals);
                currentSample[i] = (-1.0* numAttributeVals) /2.0 +0.5 + stateNumber;
                proposedSample[i] = currentSample[i];
            }
            else  {
                stateNumber = initialAttributeVal.nextInt(numRawResponseStates);
                currentSample[i] = -1.0*numRawResponseStates/2.0 +0.5 + stateNumber;
                proposedSample[i] = currentSample[i];

            }
            System.out.println(currentSample[i] + " lower bound for attributeVals: " + ((-1.0* numAttributeVals)/2.0 + 0.5) + " rand int " + stateNumber );
        }

        //System.exit(0);
        for (globalCount = 0; globalCount < stepsBeforeMixingSetsIn; globalCount++ )  {
            updateCurrentSample("sampleResponseTrue");
    //        if (globalCount%1001==0) System.out.printf("repsonse %f %n", currentSample[currentSample.length - 1]);
        }
    }

    private void initializeAttributeProperties(int numPredictiveAttributes, int numNoiseAttributes, int numAttributeVals, double degreeOfCovariance, int numRawResponseStates, double stdsAboveTheMeanForPositiveResponse, double monteCarloMoveProbability) {

        this.attributeValueGenerator = new Random();
        this.probabilityOfMakingAMonteCarloMove = new Random();
        this.monteCarloMoveProbability = monteCarloMoveProbability;
        this.numPredictiveAttributes = numPredictiveAttributes;
        this.numAttributes = numNoiseAttributes + numPredictiveAttributes;
        this.numVariables = numAttributes + 1;
        this.variableNumberOfResponse = numVariables - 1;
        this.numAttributeVals = numAttributeVals;
        this.degreeOfCovariance = degreeOfCovariance;
        this.numRawResponseStates = numRawResponseStates;
        this.stdsAboveTheMeanForPositiveResponse = stdsAboveTheMeanForPositiveResponse;
    }

    private void initializeMultiVariateNormalDistribution() {
        double means[] = new double[numVariables];
        double covarianceMatrix[][] = setCovarianceMatrix();
        //printMatrix(covarianceMatrix);
        variableDistribution = new MultivariateNormalDistribution(means, covarianceMatrix);
    }

    private void printMatrix(double matrix[][]) {
        for (int i=0; i<matrix[0].length; i++)  {
            for (int j=0; j<matrix[0].length; j++)
                System.out.println(matrix[i][j] + " ");
            System.out.println("\n");
        }
    }

    private double[][] setCovarianceMatrix () {
        Random rand = new Random();
        double covarianceMatrix[][] = new double[numVariables][numVariables];
        double varianceOfAttributes = setVarianceOfAttributes();
        double varianceOfResponse = setVarianceOfResponse();
        double maxCovarianceOfAttributeWithResponse = Math.sqrt(varianceOfAttributes*varianceOfResponse);

        for (int row=0; row<numVariables; row++)
            for (int col = row; col< numVariables; col++)
                if ( isAnAttributeVariance(row, col) )
                    covarianceMatrix[row][col] = varianceOfAttributes;
                else if ( isAPredictiveCovariance(row, col) ) {
                    covarianceMatrix[row][col] = setCovariance(varianceOfAttributes, rand);
                    covarianceMatrix[col][row] = covarianceMatrix[row][col];
                }
                else if ( isACovarianceWithResponse(row, col) )  {
                    covarianceMatrix[row][col] = setCovariance(maxCovarianceOfAttributeWithResponse, rand);
                    covarianceMatrix[col][row] = covarianceMatrix[row][col];
                }
                else if ( isVarianceOfResponse(row, col) )
                    covarianceMatrix[row][col] = varianceOfResponse;
       return covarianceMatrix;
    }

    private boolean isAnAttributeVariance(int row, int col) {
        return row==col && row < variableNumberOfResponse ? true : false;
    }

    private boolean isAPredictiveCovariance(int row, int col) {
        return (row < numPredictiveAttributes && col < numPredictiveAttributes) ? true : false;
    }

    private boolean isACovarianceWithResponse(int row, int col) {
        return (row < numPredictiveAttributes && col == variableNumberOfResponse) ? true : false;
    }

    private boolean isVarianceOfResponse(int row, int col) {
        return row == variableNumberOfResponse && col == variableNumberOfResponse ? true : false;
    }

    private double setVarianceOfAttributes() {
        double variance = 0;
        double valProbability = 1.0/numAttributeVals;
        double val = 0;
        for (int i=0; i < numAttributeVals; i++)  {
            val = 0.5 - numAttributeVals/2.0 + i;
            variance += valProbability*val*val;
        //    System.out.println("variance of attributes" + variance + "numAttributeVals " + numAttributeVals + " i " +i);

        }
        System.out.println("variance of attributes" + variance + "numAttributeVals " + numAttributeVals);
        return variance;
    }

    private double setVarianceOfResponse() {
        double rawValueAssociatedWithPositiveResponse = numRawResponseStates/2.0 - 0.5;
        double standardDeviation = rawValueAssociatedWithPositiveResponse/ stdsAboveTheMeanForPositiveResponse;
        double variance = standardDeviation * standardDeviation;
        System.out.println("variance of response " + variance);
        return variance;
    }

    private double setCovariance(double maxCovariance, Random random)  {
        double centeredRandom = -1.0 + 2.0*random.nextDouble();
        return maxCovariance*degreeOfCovariance*centeredRandom;
    }

    private void initializeRandomForestProperties(int instances, int numTrees, int maxDepth) {
        this.instances = instances;
        this.numTrees = numTrees;
        this.maxDepth = maxDepth;
    }

    private RandomForest getRandomForest(List<Instance> trainingData) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.7);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        return randomForestBuilder.buildPredictiveModel(trainingData);
    }

    private  List<Instance> createTrainingData() {
        List<Instance> trainingData = Lists.newArrayList();
        Instance instance;

        for (globalCount = 0; globalCount < instances; globalCount++)  {
            instance = sampleInstance("sampleResponseTrue");
            trainingData.add(instance);
            if (globalCount%1001==0)
                System.out.printf("repsonse %f %n", currentSample[currentSample.length - 1]);
        }
      //  System.out.println("instances " + instances );
        return trainingData;
    }

    private Instance sampleInstance(String sampleResponse) {
        updateCurrentSample(sampleResponse);
        SampleInstance sampleInstance = new SampleInstance(currentSample);
        return new Instance(sampleInstance.attributes, sampleInstance.response );
    }

    private void updateCurrentSample(String sampleResponse) {
        getProposedSample();
        setProposedSampleToCurrentSampleIfItPassesAcceptanceCriterion(sampleResponse);
    }

    private void getProposedSample() {
        for (int variableNumber = 0; variableNumber < numPredictiveAttributes; variableNumber++) //only get proposal for predictiven attributes
             getProposedVariableMove(variableNumber, numAttributeVals);
        getProposedVariableMove(variableNumberOfResponse, numRawResponseStates);
    }

    private void getProposedVariableMove(int variableNumber, int numVariableVals) {
        if (probabilityOfMakingAMonteCarloMove.nextDouble() < monteCarloMoveProbability)
            return;
        else {
            if (variableValNotExtrema(variableNumber, numVariableVals))
                proposeMoveForNonExtrema(variableNumber);
            else if (variableIsMinima(variableNumber, numVariableVals))
                proposeMoveFromMinima(variableNumber);
            else if (variableIsMaxima(variableNumber, numVariableVals))
                proposeMoveFromMaxima(variableNumber);
            else  {
                System.out.println("variable val is out of bounds: varNum: " + variableNumber + ", varVal: " + currentSample[variableNumber]);
                System.exit(0);
            }
        }
    }

    private boolean variableValNotExtrema(int variableNumber, int numVariableVals) {
        return currentSample[variableNumber]>minVal(numVariableVals) && currentSample[variableNumber]<maxVal(numVariableVals);
    }
    private boolean variableIsMinima(int variableNumber, int numVariableVals) {
        return currentSample[variableNumber]==minVal(numVariableVals);
    }
    private boolean variableIsMaxima(int variableNumber, int numVariableVals) {
        return currentSample[variableNumber] == maxVal(numVariableVals);
    }

    private double maxVal(int numVariableVals) {
        return 1.0*numVariableVals/2.0 - 0.5;
    }

    private double minVal(int numVariableVals) {
        return -1.0*numVariableVals/2.0 + 0.5;
    }

    private void proposeMoveForNonExtrema(int variableNumber)  {
        double rand = attributeValueGenerator.nextDouble();
        if (rand < 1.0/3)
            proposedSample[variableNumber] = currentSample[variableNumber]-1.0;
        else if (rand < 2.0/3)
            proposedSample[variableNumber] = currentSample[variableNumber];
        else if (rand <= 1.0)
            proposedSample[variableNumber] = currentSample[variableNumber]+1.0;
        else {
            System.out.println("rand is off in move prop");
            System.exit(0);
        }
    }

    private void proposeMoveFromMinima(int variableNumber) {
        double rand = attributeValueGenerator.nextDouble();
        if (rand < 1.0/3)
            proposedSample[variableNumber] = currentSample[variableNumber]+1;
        else if (rand <= 1.0)
            proposedSample[variableNumber] = currentSample[variableNumber];
        else {
            System.out.println("rand is off in move prop");
            System.exit(0);
        }
    }

    private void proposeMoveFromMaxima(int variableNumber) {
        double rand = attributeValueGenerator.nextDouble();
        if (rand < 1.0/3)
            proposedSample[variableNumber] = currentSample[variableNumber]-1.0;
        else if (rand <= 1.0)
            proposedSample[variableNumber] = currentSample[variableNumber];
        else {
            System.out.println("rand is off in move prop");
            System.exit(0);
        }
    }

    private void setProposedSampleToCurrentSampleIfItPassesAcceptanceCriterion(String sampleResponse) {
        double ratioOfProbabilityDensities = 0;
        if (sampleResponse.equals("sampleResponseTrue"))
            ratioOfProbabilityDensities = variableDistribution.density(proposedSample)/variableDistribution.density(currentSample);
        else if (sampleResponse.equals("sampleResponseFalse"))
            ratioOfProbabilityDensities = getMarginalProbabilityForObservingAttributes(proposedSample)/getMarginalProbabilityForObservingAttributes(currentSample);

        else
            System.out.println("invalid choice for sampleResponse");

        if (ratioOfProbabilityDensities > 1)
            for (int i=0; i < numVariables; i++)  {
                currentSample[i] = proposedSample[i];
                if (i!=variableNumberOfResponse && (currentSample[i] == -2.0 || currentSample[i] == -1.0 || currentSample[i] == 0 || currentSample[i] == 1.0 || currentSample[i] == 2.0))
                    System.out.printf("currentSample[%d] %f, proposedSample[%d] %f %n", i, currentSample[i], i, proposedSample[i]);
            }
    }

    class SampleInstance {
        public Attributes attributes;
        public double response;

        public SampleInstance(double sample[]) {
            attributes =  new HashMapAttributes();
            for (int attributeNumber = 0; attributeNumber < numAttributes; attributeNumber++)
                attributes.put(Integer.toString(attributeNumber), sample[attributeNumber]);
            setResponse(sample);
        }

        private void setResponse (double sample[]) {
            double rawResponse = sample[sample.length - 1];
            double maxRawResponse = numRawResponseStates/2.0 - 0.5;
            response = (rawResponse > maxRawResponse - .0001) ? 1.0 : 0;


            if (response ==1 && randomForest == null)  {
                System.out.println("example of positive response");
                System.exit(0);
            }
           // System.out.println("response" + response + "rawResponse" + rawResponse + "maxRawResponse" + maxRawResponse);
          //  System.exit(0);
        }

    }
*/
}






