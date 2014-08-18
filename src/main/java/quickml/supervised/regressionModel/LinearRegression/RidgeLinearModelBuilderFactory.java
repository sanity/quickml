package quickml.supervised.regressionModel.LinearRegression;

import com.google.common.collect.Maps;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Map;

/**
 * Created by alexanderhawk on 8/15/14.
 */
public class RidgeLinearModelBuilderFactory implements PredictiveModelBuilderFactory<double [], RidgeLinearModel, RidgeLinearModelBuilder> {
    private static final String REGULARIZATION_CONSTANT = "regularizationConstant";


    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.put(REGULARIZATION_CONSTANT, new FixedOrderRecommender(0.001, 0.003, .01, 0.03, 0.1, 0.3));
        return parametersToOptimize;
    }

    @Override
    public RidgeLinearModelBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        return new RidgeLinearModelBuilder().regularizationConstant((Double)predictiveModelParameters.get(REGULARIZATION_CONSTANT));
    }
}
