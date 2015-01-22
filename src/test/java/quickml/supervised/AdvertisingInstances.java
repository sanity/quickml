package quickml.supervised;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.Benchmarks;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.utlities.CSVToInstanceReader;
import quickml.utlities.CSVToInstanceReaderBuilder;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by alexanderhawk on 12/30/14.
 */
public class AdvertisingInstances {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisingInstances.class);

    public static List<Instance<AttributesMap>> getAdvertisingInstances(){
       CSVToInstanceReader csvToInstanceReader = new CSVToInstanceReaderBuilder().collumnNameForLabel("outcome").buildCsvReader();
       ArrayList<Instance<AttributesMap>> advertisingInstances = Lists.newArrayList();

       try {
          final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("advertisingData.csv.gz")))));
           advertisingInstances = csvToInstanceReader.readCsvFromReader(br);

       } catch (Exception e) {
           logger.error("failed to get advertising instances");
           throw new RuntimeException("failed to get advertising instances");

       }
       return advertisingInstances;
   }

   public static class AdvertisingDateTimeExtractor implements DateTimeExtractor<AttributesMap> {
        @Override
        public DateTime extractDateTime(Instance<AttributesMap> instance) {
            AttributesMap attributes = instance.getAttributes();
            int year = 2014, month = 7, day = 1, hour = 0, minute = 0;
            if (attributes.containsKey("timeOfArrival-year")) {
                year = ((Long) attributes.get("timeOfArrival-year")).intValue();
            }
            if (attributes.containsKey("timeOfArrival-monthOfYear")) {
                month = ((Long) attributes.get("timeOfArrival-monthOfYear")).intValue();
            }
            if (attributes.containsKey("timeOfArrival-dayOfMonth")) {
                day = ((Long) attributes.get("timeOfArrival-dayOfMonth")).intValue();
            }
            if (attributes.containsKey("timeOfArrival-hourOfDay")) {
                hour = ((Long) attributes.get("timeOfArrival-hourOfDay")).intValue();
            }
            return new DateTime(year, month, day, hour, minute, 0, 0);
        }
    }
}
