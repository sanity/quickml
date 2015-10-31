package quickml.supervised.classifier.logisticRegression;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.EnhancedPredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.dataProcessing.instanceTranformer.ProductFeatureAppender;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 10/9/15.
 */

public class LogisticRegressionBuilder<I extends ClassifierInstance, R extends SparseClassifierInstance, D extends LogisticRegressionDTO<R,D>> implements EnhancedPredictiveModelBuilder<LogisticRegression, I, R, D> {
    public boolean calibrateWithPoolAdjacentViolators = false;
    public static final String MIN_OBSERVATIONS_OF_ATTRIBUTE= "minObservationsOfAttribute";
    public static final String PRODUCT_FEATURE_APPENDER = "productFeatureAppender";
    public static final String CALIBRATE_WITH_POOL_ADJACENT_VIOLATORS = "calibrateWithPoolAdjacentViolators";
    public static final String POOL_ADJACENT_VIOLATORS_MIN_WEIGHT = "poolAdjacentViolatorsMinWeight";

    public LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer();

    private int minObservationsOfAttribute = 0;
    private ProductFeatureAppender<ClassifierInstance> productFeatureAppender;
    private DataTransformer<I, R, D> dataTransformer;
    GradientDescent<R> gradientDescent = new SparseSGD();
    private int minWeightForPavBuckets =2;

    public LogisticRegressionBuilder(DataTransformer<I, R, D> dataTransformer) {
        this.dataTransformer = dataTransformer;
    }

    public LogisticRegressionBuilder<I, R, D> productFeatureAppender(ProductFeatureAppender<ClassifierInstance> productFeatureAppender) {
        this.productFeatureAppender = productFeatureAppender;
        logisticRegressionDataTransformer.productFeatureAppender(productFeatureAppender);
        return this;
    }

    public LogisticRegressionBuilder<I, R, D> minObservationsOfAttribute(int minObservationsOfAttribute) {
        this.minObservationsOfAttribute = minObservationsOfAttribute;
        logisticRegressionDataTransformer.minObservationsOfAttribute(minObservationsOfAttribute);
        return this;
    }

    public LogisticRegressionBuilder gradientDescent(GradientDescent gradientDescent) {
        this.gradientDescent = gradientDescent;
        return this;
    }

    public LogisticRegressionBuilder calibrateWithPoolAdjacentViolators(boolean calibrateWithPoolAdjacentViolators) {
        this.calibrateWithPoolAdjacentViolators = calibrateWithPoolAdjacentViolators;
        return this;
    }

    public LogisticRegressionBuilder poolAdjacentViolatorsMinWeight(int minWeightForPavBuckets) {
        this.minWeightForPavBuckets = minWeightForPavBuckets;
        return this;
    }

    @Override
    public D transformData(List<I> instances){
        return dataTransformer.transformData(instances);
    }

    @Override
    public LogisticRegression buildPredictiveModel(D logisticRegressionDTO) {
        List<R> sparseClassifierInstances =logisticRegressionDTO.getTransformedInstances();
        double[] weights = gradientDescent.minimize(sparseClassifierInstances, logisticRegressionDTO.getNameToIndexMap().size());
        LogisticRegression uncalibrated = getUncalibratedModel(logisticRegressionDTO, weights);
        if (calibrateWithPoolAdjacentViolators) {
            PoolAdjacentViolatorsModel poolAdjacentViolatorsModel =
                    new PoolAdjacentViolatorsModel(LogisticRegressionBuilder.<R>getPavPredictions(logisticRegressionDTO.getTransformedInstances(),
                            uncalibrated), minWeightForPavBuckets);
            return new LogisticRegression(uncalibrated, poolAdjacentViolatorsModel);
        }
        return uncalibrated;
    }

    private LogisticRegressionDTO getLogisticRegressionDTO(Iterable<? extends ClassifierInstance> trainingData) {
        List<ClassifierInstance> trainingDataList = Utils.iterableToListOfClassifierInstances(trainingData);
        return logisticRegressionDataTransformer.transformData(trainingDataList);
    }

    //modify to take the dto and the weight
    private LogisticRegression getUncalibratedModel(LogisticRegressionDTO logisticRegressionDTO, double[] weights) {
        LogisticRegression uncalibrated;
        if (logisticRegressionDTO.getNumericClassLabels() == null) {
            Set<Double> classifications = InstanceTransformerUtils.getClassifications(logisticRegressionDTO.getTransformedInstances());
             uncalibrated = new LogisticRegression(weights, logisticRegressionDTO.getNameToIndexMap(), classifications);
        } else {
             uncalibrated =  new LogisticRegression(weights,logisticRegressionDTO.getNameToIndexMap(),logisticRegressionDTO.getNumericClassLabels());
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
        if (config.containsKey(MIN_OBSERVATIONS_OF_ATTRIBUTE)) {
            minObservationsOfAttribute((Integer) config.get(MIN_OBSERVATIONS_OF_ATTRIBUTE));
        }
        if (config.containsKey(PRODUCT_FEATURE_APPENDER)) {
            productFeatureAppender((ProductFeatureAppender<ClassifierInstance>) config.get(PRODUCT_FEATURE_APPENDER));
        }
        if (config.containsKey(CALIBRATE_WITH_POOL_ADJACENT_VIOLATORS)) {
            calibrateWithPoolAdjacentViolators((Boolean) config.get(CALIBRATE_WITH_POOL_ADJACENT_VIOLATORS));
        }
        if (config.containsKey(POOL_ADJACENT_VIOLATORS_MIN_WEIGHT)) {
            poolAdjacentViolatorsMinWeight((Integer) config.get(POOL_ADJACENT_VIOLATORS_MIN_WEIGHT));
        }
    }
}
