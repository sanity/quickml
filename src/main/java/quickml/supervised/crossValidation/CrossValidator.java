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
public abstract class CrossValidator<R, L, P> {
    public abstract <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R,L, PM> predictiveModelBuilder, Iterable<? extends Instance<R, L>> allTrainingData);
    public abstract <PM extends PredictiveModel<R, P>,  PMB extends PredictiveModelBuilder<R, L, PM>> List<Pair<String, MultiLossFunctionWithModelConfigurations<L, P>>> getAttributeImportances(PredictiveModelBuilderFactory<R, L, PM, PMB> predictiveModelBuilderFactory, Map<String, Object> config, Iterable<? extends Instance<R, L>> rawTrainingData, final String primaryLossFunction, Set<String> attributes, Map<String, CrossValLossFunction<L,P>> lossFunctions);
}