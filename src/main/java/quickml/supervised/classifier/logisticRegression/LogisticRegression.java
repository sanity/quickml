package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.MathUtils;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.Serializable;
import java.util.*;


/**
 * Created by alexanderhawk on 10/9/15.
 */
public class LogisticRegression extends AbstractClassifier {

    double[] weights;
    private final HashMap<String, Integer> nameToIndexMap;
    private static final Logger logger = LoggerFactory.getLogger(LogisticRegression.class);
    private  Map<Serializable, Double> classificationToClassNameMap;
    private PoolAdjacentViolatorsModel poolAdjacentViolatorsModel;
    private Set<Double> classifications =Sets.newHashSet();

    public LogisticRegression(LogisticRegression uncalibrated, PoolAdjacentViolatorsModel poolAdjacentViolatorsModel) {
        this.poolAdjacentViolatorsModel = poolAdjacentViolatorsModel;
        this.weights = uncalibrated.weights;
        this.nameToIndexMap = uncalibrated.nameToIndexMap;
        this.classificationToClassNameMap = uncalibrated.classificationToClassNameMap;
        this.classifications = uncalibrated.classifications;
    }

    public LogisticRegression(double[] weights, final HashMap<String, Integer> nameToIndexMap,
                              Map<Serializable, Double> classificationToClassNameMap) {
        this.weights = weights;
        this.nameToIndexMap = nameToIndexMap;
        this.classificationToClassNameMap = classificationToClassNameMap;
        for (Double classification: classificationToClassNameMap.values()) {
            classifications.add(classification);
        }
    }

    public LogisticRegression(double[] weights, final HashMap<String, Integer> nameToIndexMap,
                              Set<Double> classifications) {
        this.weights = weights;
        this.nameToIndexMap = nameToIndexMap;
        this.classifications= classifications;
    }


    @Override
    public double getProbability(final AttributesMap attributes, final Serializable classification) {
        double dotProduct = 0;
        dotProduct += weights[0];
        for (String attribute : attributes.keySet()) {
            int index = nameToIndexMap.get(attribute);
            dotProduct += weights[index] * (Double) attributes.get(attribute);
        }
        double uncalibrated;
        if ((double)classification == 1.0) {
            uncalibrated =  MathUtils.sigmoid(dotProduct);
        } else {
            uncalibrated =  1.0-MathUtils.sigmoid(dotProduct);
        }
        if (poolAdjacentViolatorsModel!=null) {
            return poolAdjacentViolatorsModel.predictIfInterpolation(uncalibrated);
        } else {
            return uncalibrated;
        }
    }

    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        PredictionMap predictionMap = new PredictionMap(new HashMap<Serializable, Double>());
        for (Serializable classification : classifications) {
            predictionMap.put(classification, getProbability(attributes, classification));
        }
        return predictionMap;
    }


    @Override
    public PredictionMap predictWithoutAttributes(final AttributesMap attributes, final Set<String> attributesToIgnore) {
        throw new RuntimeException("not implemented");
    }

    public List<Pair<Double, String>> getTopMostPredictiveAttributes(double fractionOfList){
        List<Pair<Double, String>> topAttributes = Lists.newArrayList();
        for (Map.Entry<String, Integer> entry: nameToIndexMap.entrySet()) {
            topAttributes.add(new Pair<Double, String>(weights[entry.getValue()],entry.getKey()));
        }
        Collections.sort(topAttributes, new Comparator<Pair<Double, String>>() {
            @Override
            public int compare(Pair<Double, String> o1, Pair<Double, String> o2) {
                return Double.compare(o1.getValue0(), o2.getValue0());
            }
        });
        double attributesToReturn = Math.max(1.0, fractionOfList) * topAttributes.size();
        return topAttributes.subList(0, (int)attributesToReturn);
    }

}

