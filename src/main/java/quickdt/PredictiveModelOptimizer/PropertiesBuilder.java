package quickdt.predictiveModelOptimizer;

import java.util.List;

public class PropertiesBuilder {
    private String name;
    private boolean isOrdinal;
    private boolean isMonotonicallyConvergent = false;
    private List<Object> range;
    private Object initialGuessOfOptimalValue;
    private boolean binarySearchTheRange;
    private double parameterTolerance;
    private double errorTolerance;

    public PropertiesBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PropertiesBuilder setIsOrdinal(boolean isOrdinal) {
        this.isOrdinal = isOrdinal;
        return this;
    }

    public PropertiesBuilder setIsMonotonicallyConvergent(boolean isMonotonicallyConvergent) {
        this.isMonotonicallyConvergent = isMonotonicallyConvergent;
        return this;
    }

    public PropertiesBuilder setRange(List<Object> range) {
        this.range = range;
        return this;
    }

    public PropertiesBuilder setInitialGuessOfOptimalValue(Object initialGuessOfOptimalValue) {
        this.initialGuessOfOptimalValue = initialGuessOfOptimalValue;
        return this;
    }

    public PropertiesBuilder setBinarySearchTheRange(boolean binarySearchTheRange) {
        this.binarySearchTheRange = binarySearchTheRange;
        return this;
    }

    public PropertiesBuilder setParameterTolerance(double parameterTolerance) {
        this.parameterTolerance = parameterTolerance;
        return this;
    }

    public PropertiesBuilder setErrorTolerance(double errorTolerance) {
        this.errorTolerance = errorTolerance;
        return this;
    }

    public Properties createProperties() {
        return new Properties(name, isOrdinal, isMonotonicallyConvergent, range, initialGuessOfOptimalValue, binarySearchTheRange, parameterTolerance, errorTolerance);
    }
}