package quickml.Utilities;

import com.google.common.base.Optional;

import java.util.List;

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
public class CSVToInstanceReaderBuilder {
    private List<String> header;
    boolean allValuesAreQuoted = false;
    private String columnNameForLabel;
    private String columnNameForWeight;
    private boolean containsUnLabeledInstances = false;
    private Optional<CategoricalSelector> categoricalSelector = Optional.absent();
    private Optional<NumericSelector> numericSelector = Optional.absent();
    private char delimiter = ',';


    public CSVToInstanceReaderBuilder categoricalSelector(CategoricalSelector categoricalSelector) {
        this.categoricalSelector = Optional.of(categoricalSelector);
        return this;
    }

    public CSVToInstanceReaderBuilder numericSelector(NumericSelector numericSelector) {
        this.numericSelector = Optional.of(numericSelector);
        return this;
    }


    public boolean containsUnLabeledInstances() {
        return containsUnLabeledInstances;
    }

    public CSVToInstanceReaderBuilder collumnNameForLabel(String columnNameForLabel) {
        this.columnNameForLabel = columnNameForLabel;
        return this;
    }

    public CSVToInstanceReaderBuilder collumnNameForWeight(String columnNameForWeight) {
        this.columnNameForWeight = columnNameForWeight;
        return this;
    }

    public CSVToInstanceReaderBuilder allValuesAreQuoted(boolean allValuesAreQuoted) {
        this.allValuesAreQuoted = allValuesAreQuoted;
        return this;
    }

    public CSVToInstanceReaderBuilder delimiter(char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVToInstanceReader buildCsvReader(){
        return new CSVToInstanceReader(delimiter, columnNameForLabel, columnNameForWeight, categoricalSelector, numericSelector);
    }

}