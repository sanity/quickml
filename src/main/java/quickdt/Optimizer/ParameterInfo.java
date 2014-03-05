package quickdt.Optimizer;

import java.util.List;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class ParameterInfo {  //make an object
    public boolean isOrdinal;
    public List range;
    public String name;
    public boolean binarySearchTheRange;
    public Object optimalValue;
    public Object optimalValueFromPreviousIteration;
    public double lossFromOptimalValue;
    public double lossFromPreviousOptimalValue;
    public double parameterTolerance = 1.0;
    public double errorTolerance;

    public ParameterInfo(boolean isOrdinal, List<Object> range, Object initialGuessOfOptimalValue, boolean binarySearchTheRange, double parameterTolerance){
        this.isOrdinal = isOrdinal;
        this.range = range;
        this.optimalValue = initialGuessOfOptimalValue;
        this.binarySearchTheRange = binarySearchTheRange;
        this.parameterTolerance = parameterTolerance;
    }

    public void setValuesFromPreviousIteration() {
        optimalValueFromPreviousIteration = optimalValue;
        lossFromPreviousOptimalValue = lossFromOptimalValue;
    }
}
