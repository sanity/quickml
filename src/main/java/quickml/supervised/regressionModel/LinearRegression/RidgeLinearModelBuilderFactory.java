package quickml.supervised.regressionModel.LinearRegression;

import com.google.common.collect.Maps;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/15/14.
 */
public class RidgeLinearModelBuilderFactory implements PredictiveModelBuilderFactory<double [], Serializable, RidgeLinearModel, RidgeLinearModelBuilder> {
    private static final String REGULARIZATION_CONSTANT = "regularizationConstant";
    private static final String INCLUDE_BIAS_TERM = "includeBiasTerm";
    private String [] header;
    private boolean includeBiasTerm = true;

    Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();


    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        if (!parametersToOptimize.containsKey(REGULARIZATION_CONSTANT)) {
            parametersToOptimize.put(REGULARIZATION_CONSTANT, new FixedOrderRecommender(0.001, 0.003, .01, 0.03, 0.1, 0.3));
        }
        return parametersToOptimize;
    }

    public RidgeLinearModelBuilderFactory regularizationConstants(FieldValueRecommender fieldValueRecommender) {
        parametersToOptimize.put(REGULARIZATION_CONSTANT, fieldValueRecommender);
        return this;
    }

    public RidgeLinearModelBuilderFactory header(String [] header) {
        this.header = header;
        return this;
    }

    public RidgeLinearModelBuilderFactory includeBiasTerm(Boolean includeBiasTerm) {
        parametersToOptimize.put(INCLUDE_BIAS_TERM, new FixedOrderRecommender(includeBiasTerm));
        return this;
    }

    @Override
    public RidgeLinearModelBuilder buildBuilder(Map<String, Object> predictiveModelParameters) {
        return new RidgeLinearModelBuilder().regularizationConstant((Double)predictiveModelParameters.get(REGULARIZATION_CONSTANT)).header(header).includeBiasTerm((Boolean) predictiveModelParameters.get(INCLUDE_BIAS_TERM));
    }
}
