package quickml.Utilities;

import com.google.common.base.Optional;
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

import quickml.Utilities.Selectors.*;

/**
 * Created by alexanderhawk on 10/2/14.
 */


/* This class converts the contents of a csv file into quickml instances.
   Defaults:
   1. the column containing the instance label is assumed to be the first collumn in the csv file.
   2. quoted variables values (either with single or double quotes) are assumed to be categorical, all others are assumed
      to be numeric.
   3. all instances are assumed to have equal weight


   Options:
   1. the column for an instances label can be specified by its name in the header in the function: columnNameForLabel.
   2. the column for an instances weight can be specified by its name in the header in the function: columnNameForWeight.
   2. One can specify which variables are categorical by providing either an instancet of a NumericSelector to numericSelector(), or
      a CategoricalSelector to categoricalSelector.  Only one of the two needs to be provided.

 */
public class CSVReader {
    private List<String> header;
    private String columnNameForLabel;
    private String columnNameForWeight;
    private boolean containsUnLabeledInstances = false;
    private Optional<CategoricalSelector> categoricalSelector = Optional.absent();
    private Optional<NumericSelector> numericSelector = Optional.absent();
    private String delimiter = ",";

    public CSVReader(){}

    public CSVReader(String delimiter, String columnNameForLabel, String columnNameForWeight, Optional<CategoricalSelector> categoricalSelector,
        Optional<NumericSelector> numericSelector) {
        this.delimiter = delimiter;
        this.columnNameForLabel = columnNameForLabel;
        this.columnNameForWeight = columnNameForWeight;
        this.categoricalSelector = categoricalSelector;
        this.numericSelector = numericSelector;
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
        Splitter splitter = Splitter.on(delimiter);
        List<String> uncleanStrings = splitter.splitToList(headerString);
        return removeQuotesAndNonVisibleCharactersAndWhiteSpacesFromString(uncleanStrings);
    }

    private Instance<AttributesMap> instanceConverter(String instanceString) {
        Splitter splitter = Splitter.on(delimiter);
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
        boolean categoricalOrNumericSelectorProvided = categoricalSelector.isPresent() || numericSelector.isPresent();
        //remove white spaces and invisible characters
        varValue = varValue.replaceAll("\\s", "");
        //perform default conversion if possible (where quoted values are taken to be categorical)
        if (!categoricalOrNumericSelectorProvided) {
            if (varValue.startsWith("\"") || varValue.startsWith("\'")) {
                return varValue.substring(1, varValue.length() - 2);
            } else {
                return tryToConvertToNumeric(varValue);
            }
        } else {
            //note: quoted values will be treated as categorical unless a selector indicates otherwise
            if (categoricalSelector.isPresent() && categoricalSelector.get().isCategorical(varName)) {
                return categoricalSelector.get().cleanValue(varValue);
            } else if (!numericSelector.isPresent() || numericSelector.get().isNumeric(varName)) {
                if (numericSelector.isPresent()) {
                    varValue = numericSelector.get().cleanValue(varValue);
                }
                return tryToConvertToNumeric(varValue);
            } else {
                //now account for the case where a numeric selector is provided, but no categorical selector is.
                return varValue;
            }
        }
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
        }
        return cleanedStrings;
    }

    public static void main(String[] args) {
        Set<String> catVariables = Sets.newHashSet();
        catVariables.add("eap");
        CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder().collumnNameForLabel("campaignId").categoricalSelector(new ExplicitCategoricalSelector(catVariables));
        CSVReader csvReader = csvReaderBuilder.buildCsvReader();
        List<Instance<AttributesMap>> instances = csvReader.readCsv("test.csv");

        for (Instance<AttributesMap> instance : instances)
            System.out.println("label: " + instance.getLabel() + "attributes: " + instance.getAttributes().toString());
    }
}