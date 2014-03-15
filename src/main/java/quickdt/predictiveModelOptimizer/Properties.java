package quickdt.predictiveModelOptimizer;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by alexanderhawk on 3/6/14.
 */
public class Properties {
    public String name;
    public boolean isOrdinal = true;
    public boolean isMonotonicallyConvergent;
    public boolean binarySearchTheRange = false;
    public Object optimalValue;
    public List<Object> range;
    public double parameterTolerance;
    public double errorTolerance;

    public Properties(String name, boolean isOrdinal, boolean isMonotonicallyConvergent, List<Object> range, Object initialGuessOfOptimalValue, boolean binarySearchTheRange, double parameterTolerance, double errorTolerance) {
        this.name = name;
        this.isOrdinal = isOrdinal;
        this.isMonotonicallyConvergent = isMonotonicallyConvergent;
        this.binarySearchTheRange = binarySearchTheRange;
        this.optimalValue = initialGuessOfOptimalValue;
        this.range = range;
        this.parameterTolerance = parameterTolerance;
        this.errorTolerance = errorTolerance;
    }
    public Properties(Properties properties){
        this.name = new String(properties.name);
        this.isOrdinal = properties.isOrdinal;
        this.isMonotonicallyConvergent = properties.isMonotonicallyConvergent;
        this.binarySearchTheRange = properties.binarySearchTheRange;
        this.optimalValue = Parameter.copyConstructAppropriatePrimitiveWrapper(properties.optimalValue);
        this.parameterTolerance = properties.parameterTolerance;
        this.errorTolerance = properties.errorTolerance;
        this.range = Lists.<Object>newArrayList();
        for(Object obj : properties.range)
            this.range.add(Parameter.copyConstructAppropriatePrimitiveWrapper(obj));
    }

}

