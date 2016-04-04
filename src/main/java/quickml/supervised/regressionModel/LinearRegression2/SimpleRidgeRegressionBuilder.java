package quickml.supervised.regressionModel.LinearRegression2;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.SparseRegressionInstance;
import quickml.supervised.EnhancedPredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.classifier.logisticRegression.LinearRegressionDTO;
import quickml.supervised.dataProcessing.instanceTranformer.ProductFeatureAppender;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/9/15.
 */


public class SimpleRidgeRegressionBuilder<D extends LinearRegressionDTO<D>> implements EnhancedPredictiveModelBuilder<LinearModel, ClassifierInstance, SparseRegressionInstance, D> {
    public boolean calibrateWithPoolAdjacentViolators = false;
    public static final String MIN_OBSERVATIONS_OF_ATTRIBUTE= "minObservationsOfAttribute";
    public static final String PRODUCT_FEATURE_APPENDER = "productFeatureAppender";
    public static final String CALIBRATE_WITH_POOL_ADJACENT_VIOLATORS = "calibrateWithPoolAdjacentViolators";
    public static final String POOL_ADJACENT_VIOLATORS_MIN_WEIGHT = "poolAdjacentViolatorsMinWeight";

    public StandardDataTransformer<D> logisticRegressionDataTransformer;

    private ProductFeatureAppender<ClassifierInstance> productFeatureAppender;
    GradientDescent<SparseRegressionInstance> gradientDescent = new SparseSGD();
    private int minWeightForPavBuckets =2;

    public SimpleRidgeRegressionBuilder(StandardDataTransformer<D> dataTransformer) {
        this.logisticRegressionDataTransformer = dataTransformer;
    }

    public SimpleRidgeRegressionBuilder<D> productFeatureAppender(ProductFeatureAppender<ClassifierInstance> productFeatureAppender) {
        logisticRegressionDataTransformer.productFeatureAppender(productFeatureAppender);
        return this;
    }

    public SimpleRidgeRegressionBuilder<D> minObservationsOfAttribute(int minObservationsOfAttribute) {
        logisticRegressionDataTransformer.minObservationsOfAttribute(minObservationsOfAttribute);
        return this;
    }

    public SimpleRidgeRegressionBuilder<D> gradientDescent(GradientDescent gradientDescent) {
        this.gradientDescent = gradientDescent;
        return this;
    }

    public SimpleRidgeRegressionBuilder<D> calibrateWithPoolAdjacentViolators(boolean calibrateWithPoolAdjacentViolators) {
        this.calibrateWithPoolAdjacentViolators = calibrateWithPoolAdjacentViolators;
        return this;
    }

    public SimpleRidgeRegressionBuilder<D> poolAdjacentViolatorsMinWeight(int minWeightForPavBuckets) {
        this.minWeightForPavBuckets = minWeightForPavBuckets;
        return this;
    }

    @Override
    public D transformData(List<ClassifierInstance> rawInstances){
            return logisticRegressionDataTransformer.transformData(rawInstances);
    }


    @Override
    public LogisticRegression buildPredictiveModel(D logisticRegressionDTO) {
        List<SparseRegressionInstance> sparseClassifierInstances =logisticRegressionDTO.getTransformedInstances();
        double[] weights = gradientDescent.minimize(sparseClassifierInstances, logisticRegressionDTO.getNameToIndexMap().size());
        LogisticRegression uncalibrated = getUncalibratedModel(logisticRegressionDTO, weights);
        if (calibrateWithPoolAdjacentViolators) {
            PoolAdjacentViolatorsModel poolAdjacentViolatorsModel =
                    new PoolAdjacentViolatorsModel(SimpleRidgeRegressionBuilder.<SparseRegressionInstance>getPavPredictions(logisticRegressionDTO.getTransformedInstances(),
                            uncalibrated), minWeightForPavBuckets);
            return new LogisticRegression(uncalibrated, poolAdjacentViolatorsModel);
        }
        return uncalibrated;
    }

    private LinearRegressionDTO getLogisticRegressionDTO(Iterable<? extends ClassifierInstance> trainingData) {
        List<ClassifierInstance> trainingDataList = Utils.iterableToListOfClassifierInstances(trainingData);
        return logisticRegressionDataTransformer.transformData(trainingDataList);
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
