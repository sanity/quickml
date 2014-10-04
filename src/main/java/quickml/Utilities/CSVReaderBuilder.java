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
public class CSVReaderBuilder {
    private List<String> header;
    boolean allValuesAreQuoted = false;
    private String columnNameForLabel;
    private String columnNameForWeight;
    private boolean containsUnLabeledInstances = false;
    private Optional<CategoricalSelector> categoricalSelector = Optional.absent();
    private Optional<NumericSelector> numericSelector = Optional.absent();
    private String delimiter = ",";


    public CSVReaderBuilder categoricalSelector(CategoricalSelector categoricalSelector) {
        this.categoricalSelector = Optional.of(categoricalSelector);
        return this;
    }

    public CSVReaderBuilder numericSelector(NumericSelector numericSelector) {
        this.numericSelector = Optional.of(numericSelector);
        return this;
    }


    public boolean containsUnLabeledInstances() {
        return containsUnLabeledInstances;
    }

    public CSVReaderBuilder collumnNameForLabel(String columnNameForLabel) {
        this.columnNameForLabel = columnNameForLabel;
        return this;
    }

    public CSVReaderBuilder collumnNameForWeight(String columnNameForWeight) {
        this.columnNameForWeight = columnNameForWeight;
        return this;
    }

    public CSVReaderBuilder allValuesAreQuoted(boolean allValuesAreQuoted) {
        this.allValuesAreQuoted = allValuesAreQuoted;
        return this;
    }

    public CSVReaderBuilder delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVReader buildCsvReader(){
        return new CSVReader(delimiter, columnNameForLabel, columnNameForWeight, categoricalSelector, numericSelector);
    }

}