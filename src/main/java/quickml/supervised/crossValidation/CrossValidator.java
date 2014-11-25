package quickml.supervised.crossValidation;

import org.javatuples.Pair;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.MultiLossFunctionWithModelConfigurations;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public abstract class CrossValidator<R, P> {
    public abstract <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> allTrainingData);
    public abstract <PM extends PredictiveModel<R, P>,  PMB extends PredictiveModelBuilder<R, PM>> List<Pair<String, MultiLossFunctionWithModelConfigurations<P>>> getAttributeImportances(PredictiveModelBuilderFactory<R, PM, PMB> predictiveModelBuilderFactory, Map<String, Object> config, Iterable<? extends Instance<R>> rawTrainingData, final String primaryLossFunction, Set<String> attributes, Map<String, CrossValLossFunction<P>> lossFunctions);
}