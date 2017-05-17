package quickml.utlities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/16/17.
 */
public class LibSVMFormatReader {

    public List<ClassifierInstance> readLibSVMFormattedInstances(String path, String dateAttribute) {
        List<ClassifierInstance> instances = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (String line; (line = br.readLine()) != null; ) {
                List<String> rawInstance = Arrays.asList(line.split(" "));
                Double label = Double.valueOf(rawInstance.get(0));
                AttributesMap map = AttributesMap.newHashMap();
                DateTime instanceTimeStamp = null;
                for (String rawAttributeAndValue : rawInstance.subList(1, rawInstance.size())) {
                    String[] attributeAndValue = rawAttributeAndValue.split(":");
                    String attribute = attributeAndValue[0];
                    String value = attributeAndValue[1];
                    if (attribute.equals(dateAttribute)) {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"); //format of T may be wrong
                        instanceTimeStamp = new DateTime(dateTimeFormatter.parseMillis((String) value));
                    } else {
                        try {
                            //add numeric variable as Double
                            map.put(attribute, Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            //add categorical variable as String
                            map.put(attribute, value);
                        }
                    }
                }
                instances.add(new ClassifierInstance(map, label, instanceTimeStamp));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return instances;
    }
}


