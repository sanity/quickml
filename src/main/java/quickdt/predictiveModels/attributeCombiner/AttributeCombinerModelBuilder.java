package quickdt.predictiveModels.attributeCombiner;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created by ian on 3/28/14.
 */
public class AttributeCombinerModelBuilder implements UpdatablePredictiveModelBuilder<AttributeCombinerPredictiveModel> {
    private static final  Logger logger = LoggerFactory.getLogger(AttributeCombinerModelBuilder.class);

    private final TreeBuilder preBuilder;
    private final PredictiveModelBuilder wrappedBuilder;
    private final Set<List<String>> attributesToCombine;

    public AttributeCombinerModelBuilder(Set<Set<String>> attributesToCombine) {
        this(new RandomForestBuilder(), attributesToCombine);
    }

    public AttributeCombinerModelBuilder(PredictiveModelBuilder wrappedBuilder, Set<Set<String>> attributesToCombine) {
        this(new TreeBuilder().maxDepth(3), wrappedBuilder, attributesToCombine);
    }

    public AttributeCombinerModelBuilder(TreeBuilder preBuilder, PredictiveModelBuilder<PredictiveModel> wrappedBuilder, Set<Set<String>> attributesToCombine) {
        this.wrappedBuilder = wrappedBuilder;
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
        final PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(enrichedTrainingData);

        return new AttributeCombinerPredictiveModel(predictiveModel, attributeEnricher);
    }

    @Override
    public PredictiveModelBuilder<AttributeCombinerPredictiveModel> updatable(boolean updatable) {
        wrappedBuilder.updatable(updatable);
        return this;
    }


    protected AttributeEnricher getAttributeEnricher(Iterable<? extends AbstractInstance> trainingData) {
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

    @Override
    public void updatePredictiveModel(AttributeCombinerPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            final AttributeEnricher attributeEnricher = getAttributeEnricher(newData);
            final Iterable<Instance> enrichedTrainingData = Lists.newLinkedList(Iterables.transform(newData, new InstanceModifier(attributeEnricher)));
            ((UpdatablePredictiveModelBuilder)wrappedBuilder).updatePredictiveModel(predictiveModel.predictiveModel, enrichedTrainingData, trainingData, splitNodes);
        }
    }

    @Override
    public void stripData(AttributeCombinerPredictiveModel predictiveModel) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            ((UpdatablePredictiveModelBuilder)wrappedBuilder).stripData(predictiveModel.predictiveModel);
        }
    }


}
