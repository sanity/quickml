package quickdt.predictiveModels.attributeCombiner;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModel;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModelBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created by ian on 3/28/14.
 */
public class AttributeCombinerModelBuilder extends WrappedPredictiveModelBuilder {
    private static final  Logger logger =  LoggerFactory.getLogger(AttributeCombinerModelBuilder.class);

    private final TreeBuilder preBuilder;
    private final Set<List<String>> attributesToCombine;

    public AttributeCombinerModelBuilder(Set<Set<String>> attributesToCombine) {
        this(new RandomForestBuilder(), attributesToCombine);
    }

    public AttributeCombinerModelBuilder(PredictiveModelBuilder<?> wrappedBuilder, Set<Set<String>> attributesToCombine) {
        this(new TreeBuilder().maxDepth(3), wrappedBuilder, attributesToCombine);
    }

    public AttributeCombinerModelBuilder(TreeBuilder preBuilder, PredictiveModelBuilder<?> wrappedBuilder, Set<Set<String>> attributesToCombine) {
        super(wrappedBuilder);
        this.preBuilder = preBuilder;
        this.attributesToCombine = Sets.newHashSet();
        for (Set<String> attributes : attributesToCombine) {
            this.attributesToCombine.add(Lists.newArrayList(attributes));
        }
    }

    @Override
    public AttributeCombinerPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        final AttributeEnricher attributeEnricher = getAttributeEnricher(trainingData);
        final Iterable<Instance> enrichedTrainingData = Lists.newLinkedList(Iterables.transform(trainingData, new InstanceModifier(attributeEnricher)));

        logger.info("Building main predictive model");
        final PredictiveModel predictiveModel = wrappedPredictiveModelBuilder.buildPredictiveModel(enrichedTrainingData);

        return new AttributeCombinerPredictiveModel(predictiveModel, attributeEnricher);
    }

    @Override
    public void updatePredictiveModel(WrappedPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        final AttributeEnricher attributeEnricher = getAttributeEnricher(newData);
        final Iterable<Instance> enrichedTrainingData = Lists.newLinkedList(Iterables.transform(newData, new InstanceModifier(attributeEnricher)));
        super.updatePredictiveModel(predictiveModel, enrichedTrainingData, trainingData, splitNodes);
    }


    private AttributeEnricher getAttributeEnricher(Iterable<? extends AbstractInstance> trainingData) {
        List<AttributePreprocessor> attributePreprocessors = Lists.newArrayList();
        for (List<String> attributes : attributesToCombine) {
            final String key = Joiner.on('|').join(attributes);
            logger.info("Building predictive model for "+key);
            AttributePreprocessor attributePreprocessor = new AttributePreprocessor();
            attributePreprocessor.key = key;
            attributePreprocessor.keys = attributes;
            Iterable<? extends AbstractInstance> filteredTrainingData = Iterables.transform(trainingData, new InstanceAttributeFilter(attributes));
            attributePreprocessor.tree = preBuilder.buildPredictiveModel(filteredTrainingData);
            attributePreprocessors.add(attributePreprocessor);
        }

        return new AttributeEnricher(attributePreprocessors);
    }
}
