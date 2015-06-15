package quickml.supervised;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.BenchmarkTest;
import quickml.data.ClassifierInstance;
import quickml.data.InstanceWithAttributesMap;
import quickml.utlities.CSVToInstanceReader;
import quickml.utlities.CSVToInstanceReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by alexanderhawk on 12/30/14.
 */
public class InstanceLoader {
    private static final Logger logger = LoggerFactory.getLogger(InstanceLoader.class);

    public static List<ClassifierInstance> getAdvertisingInstances() {
        CSVToInstanceReader csvToInstanceReader = new CSVToInstanceReaderBuilder().collumnNameForLabel("outcome").buildCsvReader();
        List<ClassifierInstance> advertisingInstances;
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("advertisingData.csv.gz")))));
            advertisingInstances = csvToInstanceReader.readCsvFromReader(br);

        } catch (Exception e) {
            logger.error("failed to get advertising instances", e);
            throw new RuntimeException("failed to get advertising instances");

        }
        return advertisingInstances;
    }


}
