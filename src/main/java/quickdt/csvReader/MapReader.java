package quickdt.csvReader;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;
import java.io.FileReader;
import java.util.Map;


 /* Created by alexanderhawk on 5/23/14.
 */
public class MapReader {
    /*

    private static Map<String, Object> readWithCsvMapReader(String inputFile) throws Exception {

        ICsvMapReader mapReader = null;
        Map<String, Object> customerMap;
        try {
            mapReader = new CsvMapReader(new FileReader(inputFile), CsvPreference.STANDARD_PREFERENCE);

            // the header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            while( (customerMap = mapReader.read(header, processors)) != null ) {
                System.out.println(String.format("lineNo=%s, rowNo=%s, customerMap=%s", mapReader.getLineNumber(),
                        mapReader.getRowNumber(), customerMap));
            }

        }
        finally {
            if( mapReader != null ) {
                mapReader.close();
            }
        }
        return customerMap;
    }
   */
}
