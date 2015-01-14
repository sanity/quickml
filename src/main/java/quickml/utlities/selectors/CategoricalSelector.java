package quickml.utlities.selectors;

/**
 * Created by alexanderhawk on 10/4/14.
 */
public interface CategoricalSelector {
    boolean isCategorical(String columnName);
    String cleanValue(String value);
}
