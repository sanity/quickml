package quickml.supervised.regressionModel.LinearRegression2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModel;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by alexanderhawk on 8/12/14.
 */
public class LinearModel implements PredictiveModel<AttributesMap, Double> {
    private static final Logger logger = LoggerFactory.getLogger(LinearModel.class);
    double[] weights;
    private final HashMap<String, Integer> nameToIndexMap;
    boolean useBias = true;


    public LinearModel(double[] weights, final HashMap<String, Integer> nameToIndexMap) {
        this.weights = weights;
        this.nameToIndexMap = nameToIndexMap;
    }

    public LinearModel(double[] weights, final HashMap<String, Integer> nameToIndexMap,
                       boolean useBias) {
        this.weights = weights;
        this.nameToIndexMap = nameToIndexMap;
        this.useBias = useBias;
    }


    @Override
    public Double predict(AttributesMap attributes) {

        double dotProduct = 0;
        if (useBias) {
            dotProduct += weights[0];
        }
        for (String attribute : attributes.keySet()) {
            int index = nameToIndexMap.get(attribute);
            dotProduct += weights[index] * (Double) attributes.get(attribute);
        }
        return dotProduct;
    }


    @Override
    public Double predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        boolean currentlyUnsupported = true;
        if (currentlyUnsupported) {
            throw new UnsupportedOperationException("this operation is currently unsupported");
        }
        return Double.valueOf(0.0);
    }


}
