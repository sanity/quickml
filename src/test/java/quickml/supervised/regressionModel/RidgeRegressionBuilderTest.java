package quickml.supervised.regressionModel;

import com.beust.jcommander.internal.Lists;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.instances.Instance;
import quickml.data.instances.RegressionInstance;
import quickml.data.instances.RidgeInstance;
import quickml.supervised.regressionModel.LinearRegression.RidgeLinearModel;
import quickml.supervised.regressionModel.LinearRegression.RidgeLinearModelBuilder;
import quickml.supervised.regressionModel.LinearRegression2.LinearModel;
import quickml.supervised.regressionModel.LinearRegression2.SimpleRidgeRegressionBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderhawk on 8/15/14.
 */
public class RidgeRegressionBuilderTest {
    private double regularizationConstant = 0.0;

    RidgeLinearModelBuilder ridgeLinearModelBuilder;
    SimpleRidgeRegressionBuilder<RegressionInstance> simpleRidgeRegressionBuilder = new SimpleRidgeRegressionBuilder<>().useBias(true).ridgeRegularizationConstant(regularizationConstant);
    final Logger logger = LoggerFactory.getLogger(RidgeRegressionBuilderTest.class);
    String [] header = {"temperature"};
    private List<RidgeInstance> trainingData;
    private List<RegressionInstance> regTrainingData;

    @Before
    public void setUp() {
        ridgeLinearModelBuilder = new RidgeLinearModelBuilder().header(header).includeBiasTerm(true).regularizationConstant(regularizationConstant);

        trainingData = new ArrayList<>();
        trainingData.add(new RidgeInstance(new double[]{20.0}, 88.6));
        trainingData.add(new RidgeInstance(new double[]{16.0}, 71.6));
        trainingData.add(new RidgeInstance(new double[]{19.8}, 93.3));
        trainingData.add(new RidgeInstance(new double[]{18.4}, 84.3));
        trainingData.add(new RidgeInstance(new double[]{17.1}, 80.6));
        trainingData.add(new RidgeInstance(new double[]{15.5}, 75.2));
        trainingData.add(new RidgeInstance(new double[]{14.7}, 69.7));
        trainingData.add(new RidgeInstance(new double[]{15.7}, 71.6));
        trainingData.add(new RidgeInstance(new double[]{15.4}, 69.4));
        trainingData.add(new RidgeInstance(new double[]{16.3}, 83.3));
        trainingData.add(new RidgeInstance(new double[]{15.0}, 79.6));
        trainingData.add(new RidgeInstance(new double[]{17.2}, 82.6));
        trainingData.add(new RidgeInstance(new double[]{16.0}, 80.6));
        trainingData.add(new RidgeInstance(new double[]{17.0}, 83.5));
        trainingData.add(new RidgeInstance(new double[]{14.4}, 76.3));

        regTrainingData = ridgeInstancesToRegressionInstances(trainingData);

    }

    private List<RegressionInstance> ridgeInstancesToRegressionInstances(List<RidgeInstance> ridgeInstances) {
        List<RegressionInstance> regressionInstances = Lists.newArrayList();
        String attr = "attr";
        for (RidgeInstance ridgeInstance: ridgeInstances) {
            AttributesMap attributesMap = AttributesMap.newHashMap();
            attributesMap.put(attr, ridgeInstance.getAttributes()[0]);
            regressionInstances.add(new RegressionInstance(attributesMap, (Double)ridgeInstance.getLabel()));
        }
        return regressionInstances;
    }

    @Test
    public void simpleRidgeRegressionBuilderTest (){
        LinearModel linearModel = simpleRidgeRegressionBuilder.buildPredictiveModel(regTrainingData);
        double pythonRMSE = Math.sqrt(212.32/trainingData.size());
        double pythonEpsilon = pythonRMSE/25.0;
        double mse = 0;
        for (RegressionInstance instance : regTrainingData) {
            AttributesMap attributesMap = instance.getAttributes();
            logger.info("prediction " + linearModel.predict(attributesMap) + ". label: " + instance.getLabel());
            mse+= Math.pow(linearModel.predict(attributesMap) - (Double)instance.getLabel(), 2);
            logger.info("un-normalized mse " + mse);
        }
        mse/=trainingData.size();
        double RMSE = Math.sqrt(mse);
        logger.info("rmse_per_test_instance " + RMSE + "  Python rmse: "+pythonRMSE);
        Assert.assertTrue("mse "+ RMSE + "python mse" + pythonRMSE, RMSE < pythonRMSE + pythonEpsilon);
    }

    @Test
    public void ridgeRegressionBuilderTest (){
        RidgeLinearModel ridgeLinearModel = ridgeLinearModelBuilder.buildPredictiveModel(trainingData);
        double pythonRMSE = Math.sqrt(212.32/trainingData.size());
        double pythonEpsilon = pythonRMSE/25.0;
        double mse = 0;
        for (Instance<double[], Serializable> instance : trainingData) {
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
    //TODO[mk] updateBuilderConfig this test
    public void ridgePMOTest() {
//        CrossValidator<double[] , Serializable, Double> crossValidator = new StationaryCrossValidatorBuilder().setFolds(4).setLossFunction(new SingleVariableRealValuedFunctionMSECVLossFunction()).createCrossValidator();
//        RidgeLinearModelBuilderFactory ridgeLinearModelBuilderFactory = new RidgeLinearModelBuilderFactory().header(header).includeBiasTerm(true).regularizationConstants(new FixedOrderRecommender(0.001, 0.01, 0.1));
//        PredictiveModelOptimizer<double[], Serializable, Double, RidgeLinearModel, RidgeLinearModelBuilder> predictiveModelOptimizer = new PredictiveModelOptimizer<>(ridgeLinearModelBuilderFactory, trainingData, crossValidator);
//        Map<String, Object> optimalParams = predictiveModelOptimizer.determineOptimalConfiguration();
//        for (String key : optimalParams.keySet())
//            logger.info(key+ " : " + optimalParams.get(key));
    }
  }

