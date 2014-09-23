package quickml.supervised.regressionModel;

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.StationaryCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.SingleVariableRealValuedFunctionMSECVLossFunction;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.regressionModel.LinearRegression.RidgeLinearModel;
import quickml.supervised.regressionModel.LinearRegression.RidgeLinearModelBuilder;
import quickml.supervised.regressionModel.LinearRegression.RidgeLinearModelBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/15/14.
 */
public class RidgeRegressionBuilderTest {

    RidgeLinearModelBuilder ridgeLinearModelBuilder;
    final Logger logger = LoggerFactory.getLogger(RidgeRegressionBuilderTest.class);
    String [] header = {"temperature"};

    public List<Instance<double[]>> setUp() {
        double regularizationConstant = 0.1;
        ridgeLinearModelBuilder = new RidgeLinearModelBuilder().header(header).includeBiasTerm(true).regularizationConstant(regularizationConstant);
        List<Instance<double[]>> trainingData = new ArrayList<>();
        trainingData.add(new InstanceImpl<double[]>(new double[]{20.0}, 88.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{16.0}, 71.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{19.8}, 93.3));
        trainingData.add(new InstanceImpl<double[]>(new double[]{18.4}, 84.3));
        trainingData.add(new InstanceImpl<double[]>(new double[]{17.1}, 80.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{15.5}, 75.2));
        trainingData.add(new InstanceImpl<double[]>(new double[]{14.7}, 69.7));
        trainingData.add(new InstanceImpl<double[]>(new double[]{15.7}, 71.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{15.4}, 69.4));
        trainingData.add(new InstanceImpl<double[]>(new double[]{16.3}, 83.3));
        trainingData.add(new InstanceImpl<double[]>(new double[]{15.0}, 79.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{17.2}, 82.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{16.0}, 80.6));
        trainingData.add(new InstanceImpl<double[]>(new double[]{17.0}, 83.5));
        trainingData.add(new InstanceImpl<double[]>(new double[]{14.4}, 76.3));
        return trainingData;
    }

    @Test
    public void ridgeRegressionBuilderTest (){
        List<Instance<double[]>> trainingData = setUp();
        RidgeLinearModel ridgeLinearModel = ridgeLinearModelBuilder.buildPredictiveModel(trainingData);
        double pythonRMSE = Math.sqrt(212.32/trainingData.size());
        double pythonEpsilon = pythonRMSE/25.0;
        double mse = 0;
        for (Instance<double[]> instance : trainingData) {
            double [] x = instance.getAttributes();
            logger.info("prediction " + ridgeLinearModel.predict(x) + ". label: " + instance.getLabel());
            mse+= Math.pow(ridgeLinearModel.predict(x) - (Double)instance.getLabel(), 2);
            logger.info("un-normalized mse " + mse);
        }
        mse/=trainingData.size();
        double RMSE = Math.sqrt(mse);
        logger.info("mse_per_test_instance " + mse);
        Assert.assertTrue("mse "+ RMSE + "python mse" + pythonRMSE, RMSE < pythonRMSE + pythonEpsilon);
    }

    @Test
    public void ridgePMOTest() {
        List<Instance<double[]>> trainingData = setUp();
        CrossValidator<double[], Double> crossValidator = new StationaryCrossValidator<>(4, new SingleVariableRealValuedFunctionMSECVLossFunction());
        RidgeLinearModelBuilderFactory ridgeLinearModelBuilderFactory = new RidgeLinearModelBuilderFactory().header(header).includeBiasTerm(true).regularizationConstants(new FixedOrderRecommender(0.001, 0.01, 0.1));
        PredictiveModelOptimizer<double[], Double, RidgeLinearModel, RidgeLinearModelBuilder> predictiveModelOptimizer = new PredictiveModelOptimizer<>(ridgeLinearModelBuilderFactory, trainingData, crossValidator);
        Map<String, Object> optimalParams = predictiveModelOptimizer.determineOptimalConfiguration();
        for (String key : optimalParams.keySet())
            logger.info(key+ " : " + optimalParams.get(key));
    }
  }

