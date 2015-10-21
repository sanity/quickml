package quickml;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class MathUtils {
    public static double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    public static double cappedlogBase2(double z, double minZ) {
        double x = Math.max(z, minZ);
        return Math.log(x)/Math.log(2);
    }
}
