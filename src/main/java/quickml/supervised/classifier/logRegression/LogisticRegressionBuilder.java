package quickml.supervised.classifier.logRegression;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.tree.dataProcessing.AttributeCharacteristics;
import quickml.supervised.tree.dataProcessing.BasicTrainingDataSurveyor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.getNumericClassLabels;
import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.oneHotEncode;
import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.populateNameToIndexMap;


/**
 * Created by alexanderhawk on 10/9/15.
 */
public class LogisticRegressionBuilder implements PredictiveModelBuilder<LogisticRegression, ClassifierInstance> {
    double ridgeRegularizationConstant = 0;
    double lassoRegularizationConstant = 0;
    boolean normalizeNumericFeatures = true;
    public static final String RIDGE = "ridge";
    public static final String LASSO = "lasso";


    GradientDescent gradientDescent;

    public LogisticRegressionBuilder() {

    }

    public LogisticRegressionBuilder setGradientDescent(GradientDescent gradientDescent) {
        this.gradientDescent = gradientDescent;
        return this;
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
    public LogisticRegression buildPredictiveModel(final Iterable<ClassifierInstance> trainingData) {
        List<ClassifierInstance> trainingDataList = Utils.iterableToList(trainingData);
        DataAndDataDescriptors reEncodedData = processData(trainingDataList);

        HashMap<String, Integer> nameToIndexMap = populateNameToIndexMap(trainingDataList);
        List<SparseClassifierInstance> sparseClassifierInstances = getSparseInstances(reEncodedData.instances, nameToIndexMap);

        double[] weights = gradientDescent.minimize(sparseClassifierInstances, nameToIndexMap.size());
        return new LogisticRegression(weights, nameToIndexMap, reEncodedData.nameToIndexMap, reEncodedData.meanAndStdMap);

    }

    private List<SparseClassifierInstance> getSparseInstances(Iterable<ClassifierInstance> trainingData, HashMap<String, Integer> nameToIndexMap) {
        List<SparseClassifierInstance> sparseClassifierInstances = Lists.newArrayList();
        for (ClassifierInstance instance : trainingData) {
            sparseClassifierInstances.add(new SparseClassifierInstance(instance.getAttributes(), instance.getLabel(), instance.getWeight(), nameToIndexMap));
        }
        return sparseClassifierInstances;
    }

    private DataAndDataDescriptors processData(List<ClassifierInstance> trainingData) {
        BasicTrainingDataSurveyor<ClassifierInstance> btds = new BasicTrainingDataSurveyor<ClassifierInstance>(false);
        Map<String, AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);
        List<ClassifierInstance> instances = Lists.newArrayList();
        Map<Serializable, Double> numericClassLabels = getNumericClassLabels(trainingData);
        Map<String, Utils.MeanAndStd> meansAndStds = Utils.<ClassifierInstance>getMeansAndStds(attributeCharacteristics, trainingData);

        meanNormalizeAndOneHotEncode(trainingData, attributeCharacteristics, instances, meansAndStds);
        return new DataAndDataDescriptors(numericClassLabels, instances, meansAndStds);
    }

    private void meanNormalizeAndOneHotEncode(List<ClassifierInstance> trainingData, Map<String, AttributeCharacteristics> attributeCharacteristics, List<ClassifierInstance> normalizedInstances, Map<String, Utils.MeanAndStd> meansAndStds) {
        for (ClassifierInstance instance : trainingData) {
            AttributesMap attributesMap = AttributesMap.newHashMap();
            AttributesMap rawAttributes = instance.getAttributes();
            for (String key : rawAttributes.keySet()) {
                if (attributeCharacteristics.get(key).isNumber) {
                    Utils.MeanAndStd meanAndStd = meansAndStds.get(key);
                    attributesMap.put(key, meanNormalize(rawAttributes, key, meanAndStd));
                } else {
                    attributesMap.put(oneHotEncode(key, rawAttributes.get(key)), 1.0);
                }
            }
            normalizedInstances.add(new ClassifierInstance(attributesMap, instance.getLabel()));
        }
    }

    public static double meanNormalize(AttributesMap rawAttributes, String key, Utils.MeanAndStd meanAndStd) {
        return (((Number)rawAttributes.get(key)).doubleValue() - meanAndStd.getMean())/meanAndStd.getStd();
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

    public static class DataAndDataDescriptors{
        Map<Serializable, Double> nameToIndexMap;
        List<ClassifierInstance> instances;
        Map<String, Utils.MeanAndStd> meanAndStdMap;

        public DataAndDataDescriptors(Map<Serializable, Double> nameToIndexMap, List<ClassifierInstance> instances, Map<String, Utils.MeanAndStd> meanAndStdMap) {
            this.nameToIndexMap = nameToIndexMap;
            this.instances = instances;
            this.meanAndStdMap = meanAndStdMap;
        }
    }


}
