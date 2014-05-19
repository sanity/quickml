package quickdt.crossValidation.movingAverages;

/**
 * Created by alexanderhawk on 4/29/14.
 */

import java.util.List;

public interface MovingAverage {

    public abstract double getAverage(List<Double> values);
}
