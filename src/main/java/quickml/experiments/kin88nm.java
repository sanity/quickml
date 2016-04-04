package quickml.experiments;

import org.javatuples.Pair;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.crossValidation.RegressionLossChecker;
import quickml.supervised.crossValidation.SimpleCrossValidator;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionRMSELossFunction;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomRegressionForest.RandomRegressionForest;
import quickml.supervised.ensembles.randomForest.randomRegressionForest.RandomRegressionForestBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.regressionTree.OptimizedRegressionForests;
import quickml.supervised.tree.regressionTree.RegressionTree;
import quickml.supervised.tree.regressionTree.RegressionTreeBuilder;
import quickml.utlities.CSVToInstanceReader;
import quickml.utlities.CSVToInstanceReaderBuilder;
import quickml.utlities.selectors.NumericSelector;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 9/16/15.
 */
public class kin88nm {

    public static void main(String[] args) {
        CSVToInstanceReaderBuilder csvToInstanceReaderBuilder = new CSVToInstanceReaderBuilder().numericSelector(new NumericSelector() {
            @Override
            public boolean isNumeric(String columnName) {
                return true;
            }

            @Override
            public String cleanValue(String value) {
                return value;
            }
        }).delimiter(',').collumnNameForLabel("x8").hasHeader(false);
        CSVToInstanceReader csvToInstanceReader =csvToInstanceReaderBuilder.buildCsvReader();
        try {
            List<RegressionInstance> allTrainingData = csvToInstanceReader.readRegressionInstancesFromCsv("uci-20070111-kin8nm.csv");
            List<RegressionInstance> trainData = csvToInstanceReader.readRegressionInstancesFromCsv("/Users/alexanderhawk/msda-denoising/spearmint/data/kin8nm_train.csv");
            List<RegressionInstance> valData = csvToInstanceReader.readRegressionInstancesFromCsv("/Users/alexanderhawk/msda-denoising/spearmint/data/kin8nm_test.csv");
            RegressionTreeBuilder<RegressionInstance> regressionTreeBuilder
                    = new RegressionTreeBuilder<>()
                    .degreeOfGainRatioPenalty(1.0)
                    .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.5))
                    .maxDepth(18)
                    .minLeafInstances(2)
                    .minSplitFraction(0.1)
                    .numNumericBins(10)
                    .numSamplesPerNumericBin(20);
            RandomRegressionForestBuilder<RegressionInstance> regressionForestBuilder = new RandomRegressionForestBuilder<>(regressionTreeBuilder).numTrees(400);
            //RegressionTree regressionTree = regressionTreeBuilder.buildPredictiveModel(trainData);


            RandomRegressionForest randomRegressionForest = regressionForestBuilder.buildPredictiveModel(trainData);
            //Pair<Map<String, Serializable>, RandomRegressionForest> randomForestPair = OptimizedRegressionForests.<RegressionInstance>getOptimizedRandomForest(trainData);
            //RandomRegressionForest randomRegressionForest = randomForestPair.getValue1();


            double loss =0;
            for (RegressionInstance instance: valData) {
                loss+=(instance.getLabel() - randomRegressionForest.predict(instance.getAttributes()))
                        *(instance.getLabel() - randomRegressionForest.predict(instance.getAttributes()));
            }
            loss=Math.sqrt(loss/valData.size());
            System.out.println("loss " + loss);

            SimpleCrossValidator simpleCrossValidator = new SimpleCrossValidator(regressionTreeBuilder,
                    new RegressionLossChecker(new RegressionRMSELossFunction()), new FoldedData(allTrainingData, 8, 8));
           // double loss=simpleCrossValidator.getLossForModel();

            System.out.println("here");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }



    }

}
