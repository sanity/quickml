package quickml.experiments;

import quickml.data.instances.ClassifierInstance;
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
        }).delimiter(',').collumnNameForLabel("y");
        CSVToInstanceReader csvToInstanceReader =csvToInstanceReaderBuilder.buildCsvReader();
        try {
            List<ClassifierInstance> allTrainingData = csvToInstanceReader.readCsv("");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }



    }

}
