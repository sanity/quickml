package quickml.utlities;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.data.AttributesMap;
import quickml.data.instances.Instance;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 10/30/14.
 */
public class InstancesToCsvWriter {

    public static void writeInstances(List<? extends Instance<AttributesMap, Serializable>> instances, String filePath) {
        List<String[]> allRows = Lists.newLinkedList();
        String[] header = getHeader(instances);
        allRows.add(header);
        for (Instance<AttributesMap, Serializable> instance : instances) {
            allRows.add(instanceToRow(instance, header));
        }
        File file = new File(filePath);
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            CSVWriter csvWriter = new CSVWriter(writer);
            csvWriter.writeAll(allRows);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String[] getHeader(List<? extends Instance<AttributesMap, Serializable>> instances) {
        Set<String> headerSet = Sets.newTreeSet();
        for (Instance<AttributesMap, Serializable> instance : instances) {
            for (String key : instance.getAttributes().keySet()) {
                headerSet.add(key);
            }
        }
        headerSet.add("label");
        headerSet.add("weight");
        return headerSet.toArray(new String[headerSet.size()]);
    }

    private static String[] instanceToRow(Instance<AttributesMap, Serializable> instance, String [] header) {
        String[] row = new String[header.length];
        for (int i = 0; i < header.length; i++) {
            if (((String) header[i]).equals("label")) {
                row[i] = instance.getLabel().toString();
            } else if (((String) header[i]).equals("weight")) {
                row[i] = Double.toString(instance.getWeight());
            } else {
                row[i] = instance.getAttributes().containsKey(header[i]) ? instance.getAttributes().get(header[i]).toString() : "";
            }

        }
        return row;
    }
}