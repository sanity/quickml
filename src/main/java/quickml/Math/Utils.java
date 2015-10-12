package quickml.math;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class Utils {
    public static double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }
}
