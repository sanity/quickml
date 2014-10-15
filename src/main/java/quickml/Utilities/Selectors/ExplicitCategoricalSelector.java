package quickml.Utilities.Selectors;

import quickml.Utilities.Selectors.CategoricalSelector;

import java.util.Set;

/**
 * Created by alexanderhawk on 10/4/14.
 */
public class ExplicitCategoricalSelector implements CategoricalSelector {
    private Set<String> selectionSet;

    public ExplicitCategoricalSelector(Set<String> selectionSet) {
        this.selectionSet = selectionSet;
    }

    public boolean isCategorical(String columnName) {
        return selectionSet.contains(columnName);
    }

    public String cleanValue(String value) {
        value = value.replaceAll("\\s", "");
        if ((value.startsWith("\"") && value.endsWith("\""))|| (value.startsWith("\'")) && value.endsWith("\'")) {
            return value.substring(1, value.length() - 2);
        }
        ;
        return value;
    }
}
