package quickdt.predictiveModelOptimizer;

import java.util.List;

/**
 * Created by alexanderhawk on 3/6/14.
 */
public class Properties {
    public String name;
    public boolean isOrdinal = true;
    public boolean binarySearchTheRange = false;
    public Object optimalValue;
    public List<Object> range;
    public double parameterTolerance = 1.0;
    public double errorTolerance = .05;

    public Properties(String name, boolean isOrdinal, List<Object> range, Object initialGuessOfOptimalValue, boolean binarySearchTheRange, double parameterTolerance, double errorTolerance) {
        this.name = name;
        this.isOrdinal = isOrdinal;
        this.binarySearchTheRange = binarySearchTheRange;
        this.optimalValue = initialGuessOfOptimalValue;
        this.range = range;
        this.parameterTolerance = parameterTolerance;
        this.errorTolerance = errorTolerance;
    }
}

