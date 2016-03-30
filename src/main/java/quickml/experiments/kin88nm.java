package quickml.experiments;

import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.regressionTree.RegressionTree;
import quickml.supervised.tree.regressionTree.RegressionTreeBuilder;
import quickml.utlities.CSVToInstanceReader;
import quickml.utlities.CSVToInstanceReaderBuilder;
import quickml.utlities.selectors.NumericSelector;

import java.util.List;

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
        }).delimiter(',').collumnNameForLabel("x0").hasHeader(false);
        CSVToInstanceReader csvToInstanceReader =csvToInstanceReaderBuilder.buildCsvReader();
        try {
            List<RegressionInstance> allTrainingData = csvToInstanceReader.readRegressionInstancesFromCsv("uci-20070111-kin8nm.csv");
            RegressionTreeBuilder<RegressionInstance> regressionTreeBuilder
                    = new RegressionTreeBuilder<>()
                    .degreeOfGainRatioPenalty(1.0)
                    .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.0))
                    .maxDepth(4)
                    .minAttributeValueOccurences(0)
                    .numNumericBins(5)
                    .numSamplesPerNumericBin(5);

            RegressionTree regressionTree = regressionTreeBuilder.buildPredictiveModel(allTrainingData);
            System.out.println("here");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }



    }

}
