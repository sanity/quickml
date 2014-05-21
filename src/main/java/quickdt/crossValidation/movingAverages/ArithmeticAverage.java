package quickdt.crossValidation.movingAverages;

import quickdt.crossValidation.movingAverages.MovingAverage;

import java.util.List;

/**
 * Created by alexanderhawk on 4/29/14.
 */
public class ArithmeticAverage implements MovingAverage {
    double average = 0;
    @Override
    public double getAverage(List<Double> values) {
        for(Double val : values)
            average += val;
        average /= values.size();
        return average;
    }
}
