package quickdt.experiments.crossValidation;

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import quickdt.*;

/**
 * Created by ian on 2/28/14.
 */
public class CrossValidator {
    private final Predicate<AbstractInstance> splitter;
    private final Supplier<? extends CrossValScorer<?>> scorerSupplier;

    public CrossValidator() {
        this(new AttributesHashSplitter(10), RMSECrossValScorer.supplier);
    }

    public CrossValidator(final Predicate<AbstractInstance> splitter, Supplier<? extends CrossValScorer<?>> scorerSupplier) {
        this.splitter = splitter;
        this.scorerSupplier = scorerSupplier;
    }

    public CrossValScorer<?> test(PredictiveModelBuilder<?> predictiveModelBuilder, Iterable<Instance> data) {
        Iterable<Instance> trainingData = Iterables.filter(data, Predicates.not(splitter));
        Iterable<Instance> testingData = Iterables.filter(data, splitter);
        PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);
        CrossValScorer<?> scorer = scorerSupplier.get();
        for (AbstractInstance instance : testingData) {
            scorer.score(predictiveModel.getProbability(instance.getAttributes(), instance.getClassification()));
        }
        return scorer;
    }
}
