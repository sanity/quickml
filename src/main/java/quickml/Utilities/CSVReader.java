package quickml.Utilities;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 10/2/14.
 */


/* This class converts the contents of a csv file into quickml instances.
   Defaults:
   1. instances are assumed to be unweighted
   2. the column containing the instance label is assumed to be the first collumn in the csv file.
   3. quoted variables values (either with single or double quotes) are assumed to be categorical, all others are assumed
      to be numeric.

   Options:
   1. the column for an instances label can be specified by its name in the header in the function: columnNameForLabel.
   2. the column for an instances weight can be specified by its name in the header in the function: columnNameForWeight.
   2. Specifying categorical variables / numeric variables (in situations where the csv file does not specify which
      variables are categorical with quotes in the manner expected by the defualt settings of this class) can be accomplished by
      by passing a set of names (Strings) for the categorical variables or the numeric variables into the functions categoricalVariables or numericVariables.
      One only needs to define one of these sets as the other will be assumed to be it's complement.


 */
public class CSVReader {
    private List<String> header;
    private Set<String> categoricalVariables = Sets.newHashSet();
    private Set<String> numericalVariables = null;
    boolean allValuesAreQuoted = false;
    boolean allCategorical = false;
    private String columnNameForLabel;
    private String columnNameForWeight;
    private boolean containsUnLabeledInstances = false;

    public CSVReader() {
    }

    public CSVReader categoricalVariables(Set<String> categoricalVariables) {
        this.categoricalVariables = categoricalVariables;
        return this;
    }

    public CSVReader numericVariables(Set<String> numericalVariables) {
        this.numericalVariables = numericalVariables;
        return this;
    }

    public CSVReader makeAllAttributesCategorical(boolean allCategorical) {
        this.allCategorical = allCategorical;
        return this;
    }

    public boolean containsUnLabeledInstances() {
        return containsUnLabeledInstances;
    }

    public CSVReader collumnNameForLabel(String columnNameForLabel) {
        this.columnNameForLabel = columnNameForLabel;
        return this;
    }

    public CSVReader collumnNameForWeight(String columnNameForWeight) {
        this.columnNameForWeight = columnNameForWeight;
        return this;
    }

    public CSVReader allValuesAreQuoted(boolean allValuesAreQuoted) {
        this.allValuesAreQuoted = allValuesAreQuoted;
        return this;
    }

    public List<Instance<AttributesMap>> readCsv(String fileName) {
        List<Instance<AttributesMap>> instances = Lists.newArrayList();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String headerString = in.readLine();
            header = parseHeader(headerString);
            while (true) {
                String instanceString = in.readLine();
                if (instanceString == null) {
                    break;
                }
                instances.add(instanceConverter(instanceString));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return instances;
    }

    private List<String> parseHeader(String headerString) {
        Splitter splitter = Splitter.on(",");
        List<String> uncleanStrings = splitter.splitToList(headerString);
        return removeQuotesAndNonVisibleCharactersAndWhiteSpacesFromString(uncleanStrings);
    }

    private Instance<AttributesMap> instanceConverter(String instanceString) {
        Splitter splitter = Splitter.on(",");
        List<String> values = splitter.splitToList(instanceString);
        AttributesMap attributesMap = AttributesMap.newHashMap();
        Serializable label = null;
        double weight = 1.0;
        for (int i = 0; i < header.size(); i++) {
            if (values.get(i).isEmpty())
                continue;

            boolean haveLabelInFirstCollumn = i == 0 && columnNameForLabel == null;
            boolean matchedCollumnToLabel = columnNameForLabel != null && columnNameForLabel.equals(header.get(i));
            if (haveLabelInFirstCollumn || matchedCollumnToLabel) {
                label = convertToNumberOrCleanedString(header.get(i), values.get(i));
                continue;
            }

            boolean matchedCollumnToWeight = columnNameForWeight != null && columnNameForWeight.equals(header.get(i));
            if (matchedCollumnToWeight) {
                weight = (Double) convertToNumberOrCleanedString(header.get(i), values.get(i));
                continue;
            }

            attributesMap.put(header.get(i), convertToNumberOrCleanedString(header.get(i), values.get(i)));
        }
        if (label == null) {
            label = "missing label";
            containsUnLabeledInstances = true;
        }

        return new InstanceImpl<AttributesMap>(attributesMap, label, weight);
    }

    private Serializable convertToNumberOrCleanedString(String varName, String varValue) {

        if (allValuesAreQuoted) {
            varValue = varValue.substring(1, varValue.length() - 2);
        } else {
            if (varValue.startsWith("\"") || varValue.startsWith("\'")) {
                return varValue.substring(1, varValue.length() - 2);
            }
        }
        if (allCategorical || categoricalVariables.contains(varName))
            return varValue;
        if (numericalVariables == null || numericalVariables.contains(varName)) {
            return tryToConvertToNumeric(varValue);
        }
        return varValue; //accounts for the case where categorical variables are defined as all variables not in numericalVariables
        // (as opposed to explicitly defining  a categoricalVariables set.
    }


    private Serializable tryToConvertToNumeric(String varValue) {
        try {
            return Long.valueOf(varValue);

        } catch (NumberFormatException e) {
            try {
                return Double.valueOf(varValue);
            } catch (NumberFormatException n) {
                return varValue;
            }
        }
    }

        private List<String> removeQuotesAndNonVisibleCharactersAndWhiteSpacesFromString(List<String> strings) {
            List<String> cleanedStrings = Lists.newArrayList();
            for (String string : strings) {
                //remove quotes
                if (string.startsWith("\"") || string.startsWith("\'"))
                    cleanedStrings.add(string.substring(1, string.length() - 2));
                else
                    cleanedStrings.add(string);
                //remove white spaces, and non visible characters
                string.replaceAll("\\s", "");
            }return cleanedStrings;
        }

    public static void main(String[] args) {
        Set<String> catVariables = Sets.newHashSet();
        catVariables.add("eap");
        CSVReader csvReader = new CSVReader().collumnNameForLabel("campaignId").categoricalVariables(catVariables);
        List<Instance<AttributesMap>> instances = csvReader.readCsv("test.csv");

        for (Instance<AttributesMap> instance : instances)
            System.out.println("label: " + instance.getLabel() + "attributes: " + instance.getAttributes().toString());
    }
}