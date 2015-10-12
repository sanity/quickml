package quickml.supervised.classifier.logisticRegression;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import com.google.common.collect.Lists;
import javafx.util.Pair;
import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.tree.dataExploration.BasicTrainingDataSurveyor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils.getNumericClassLabels;
import static quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils.oneHotEncode;
import static quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils.populateNameToIndexMap;


/**
 * Created by alexanderhawk on 10/9/15.
 */
public class LogisticRegressionBuilder implements PredictiveModelBuilder<LogisticRegression, SparseClassifierInstance> {
    double ridgeRegularizationConstant = 0;
    double lassoRegularizationConstant = 0;
    public static final String RIDGE = "ridge";
    public static final String LASSO = "lasso";
    public static final String MAX_ITS = "maxIts";

    GradientDescent gradientDescent;

    public LogisticRegressionBuilder(GradientDescent gradientDescent) {
        this.gradientDescent = gradientDescent;
    }


    public LogisticRegressionBuilder ridgeRegularizationConstant(final double ridgeRegularizationConstant) {
        this.ridgeRegularizationConstant = ridgeRegularizationConstant;
        return this;
    }

    public LogisticRegressionBuilder lassoRegularizationConstant(final double ridgeRegularizationConstant) {
        this.ridgeRegularizationConstant = ridgeRegularizationConstant;
        return this;
    }

    @Override
    public LogisticRegression buildPredictiveModel(final Iterable<SparseClassifierInstance> trainingData) {
        List<SparseClassifierInstance> trainingDataList = Utils.iterableToList(trainingData);
        HashMap<String, Integer> nameToIndexMap = populateNameToIndexMap(trainingDataList);
        Pair<Map<Serializable, Double>, List<SparseClassifierInstance>> reEncodedData = processData(trainingDataList, nameToIndexMap);
        double[] weights = gradientDescent.minimize(reEncodedData.getValue(), nameToIndexMap.size());
        return new LogisticRegression(weights, nameToIndexMap, reEncodedData.getKey());

    }

    private Pair<Map<Serializable, Double>, List<SparseClassifierInstance>> processData(List<SparseClassifierInstance> trainingData, HashMap<String, Integer> nameToIndexMap) {
        BasicTrainingDataSurveyor<SparseClassifierInstance> btds = new BasicTrainingDataSurveyor<>(false);
        Map<String, BasicTrainingDataSurveyor.AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);
        List<SparseClassifierInstance> sparseClassifierInstances = Lists.newArrayList();

        Map<Serializable, Double> numericClassLabels = getNumericClassLabels(trainingData);


        for (SparseClassifierInstance instance : trainingData) {
            AttributesMap attributesMap = AttributesMap.newHashMap();
            AttributesMap rawAttributes = instance.getAttributes();
            for (String key : rawAttributes.keySet()) {
                if (attributeCharacteristics.get(key).isNumber) {
                    attributesMap.put(key, rawAttributes.get(key));
                } else {
                    attributesMap.put(oneHotEncode(key, rawAttributes.get(key)), 1.0);
                }
            }
            sparseClassifierInstances.add(new SparseClassifierInstance(attributesMap, instance.getLabel(), nameToIndexMap));
        }
        return new Pair<>(numericClassLabels, sparseClassifierInstances);
    }


    @Override
    public void updateBuilderConfig(final Map<String, Serializable> config) {
        if (config.containsKey(RIDGE)) {
            ridgeRegularizationConstant((Double) config.get(RIDGE));
        }

        if (config.containsKey(LASSO)) {
            ridgeRegularizationConstant((Double) config.get(LASSO));
        }

    }


}
