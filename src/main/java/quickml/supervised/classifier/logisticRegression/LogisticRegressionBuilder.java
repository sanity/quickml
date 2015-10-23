package quickml.supervised.classifier.logisticRegression;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by alexanderhawk on 10/9/15.
 */
public class LogisticRegressionBuilder implements PredictiveModelBuilder<LogisticRegression, SparseClassifierInstance> {
    private  Map<Serializable, Double> classificationToClassNameMap;
    public final HashMap<String, Integer> nameToIndexMap;
    public boolean calibrateWithPoolAdjacentViolators = false;
   // public boolean calibrateWithPlattScaling = false;

    GradientDescent gradientDescent = new SGD();

    public LogisticRegressionBuilder(HashMap<String, Integer> nameToIndexMap) {
        this.nameToIndexMap = nameToIndexMap;
    }

    public LogisticRegressionBuilder gradientDescent(GradientDescent gradientDescent) {
        this.gradientDescent = gradientDescent;
        return this;
    }

    public LogisticRegressionBuilder classificationToClassNameMap(Map<Serializable, Double> classificationToClassNameMap){
        this.classificationToClassNameMap = classificationToClassNameMap;
        return this;
    }

//    public LogisticRegressionBuilder calibrateWithPlattScaling(boolean calibrateWithPlattScaling) {
//        this.calibrateWithPlattScaling = calibrateWithPlattScaling;
//        return this;
//    }

    public LogisticRegressionBuilder calibrateWithPoolAdjacentViolators(boolean calibrateWithPoolAdjacentViolators) {
        this.calibrateWithPoolAdjacentViolators = calibrateWithPoolAdjacentViolators;
        return this;
    }

    @Override
    public LogisticRegression buildPredictiveModel(final Iterable<SparseClassifierInstance> trainingData) {
        List<SparseClassifierInstance> trainingDataList = Utils.iterableToList(trainingData);
        double[] weights = gradientDescent.minimize(trainingDataList, nameToIndexMap.size());
        LogisticRegression uncalibrated = getUncalibratedModel(trainingDataList, weights);
        if (calibrateWithPoolAdjacentViolators) {
            int minWeight = 5;
            PoolAdjacentViolatorsModel poolAdjacentViolatorsModel =
                    new PoolAdjacentViolatorsModel(LogisticRegressionBuilder.<SparseClassifierInstance>getPavPredictions(trainingDataList,
                            uncalibrated), minWeight);
            return new LogisticRegression(uncalibrated, poolAdjacentViolatorsModel);
        }
        return uncalibrated;
    }

    private LogisticRegression getUncalibratedModel(List<SparseClassifierInstance> trainingDataList, double[] weights) {
        LogisticRegression uncalibrated;
        if (classificationToClassNameMap == null) {
            Set<Double> classifications = InstanceTransformerUtils.getClassifications(trainingDataList);
             uncalibrated = new LogisticRegression(weights, nameToIndexMap, classifications);
        } else {
             uncalibrated =  new LogisticRegression(weights,nameToIndexMap,classificationToClassNameMap);
        }
        return uncalibrated;
    }

    public static <I extends ClassifierInstance> List<PoolAdjacentViolatorsModel.Observation> getPavPredictions(List<I> trainingData, Classifier classifier) {
        List<PoolAdjacentViolatorsModel.Observation> observations = Lists.newArrayList();
        for (I instance : trainingData) {
            double uncalibratedProbability = classifier.getProbability(instance.getAttributes(), 1.0);
            PoolAdjacentViolatorsModel.Observation ob = new PoolAdjacentViolatorsModel.Observation(uncalibratedProbability, (Double) instance.getLabel(), instance.getWeight());
            observations.add(ob);
        }
        return observations;
    }

    @Override
    public void updateBuilderConfig(final Map<String, Serializable> config) {
        gradientDescent.updateBuilderConfig(config);
    }



}
