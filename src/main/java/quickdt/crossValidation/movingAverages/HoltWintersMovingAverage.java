package quickdt.crossValidation.movingAverages;

import java.util.List;

/**
 * Created by alexanderhawk on 4/29/14.
 */
public class HoltWintersMovingAverage implements MovingAverage  {
    double average = 0;
    private double alpha;
    private double beta;

    public HoltWintersMovingAverage(double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getAverage(List<Double> values) {

        double s = values.get(1);
        double b = values.get(1) - values.get(0);
        for(int i = 2; i < values.size(); i++) {
            double s_prev = s;
            s = alpha * values.get(i) + (1 - alpha) * (s - b);
            b = beta * (s - s_prev) + (1 - beta) * b;
        }
        return s;
    }
}
