package quickml.Utilities.Selectors;

/**
 * Created by alexanderhawk on 10/4/14.
 */
public interface NumericSelector {
    boolean isNumeric(String columnName);
    String cleanValue(String value);

}
