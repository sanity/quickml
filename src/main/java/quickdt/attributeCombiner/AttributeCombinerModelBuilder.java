package quickdt.attributeCombiner;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.*;
import quickdt.randomForest.RandomForestBuilder;

import java.util.*;

/**
 * Created by ian on 3/28/14.
 */
public class AttributeCombinerModelBuilder implements PredictiveModelBuilder<AttributeCombinerPredictiveModel> {
    private static final  Logger logger =  LoggerFactory.getLogger(AttributeCombinerModelBuilder.class);

    private final TreeBuilder preBuilder;
    private final PredictiveModelBuilder<?> wrappedBuilder;
    private final Set<List<String>> attributesToCombine;

    public AttributeCombinerModelBuilder(Set<Set<String>> attributesToCombine) {
        this(new RandomForestBuilder(), attributesToCombine);
    }

    public AttributeCombinerModelBuilder(PredictiveModelBuilder<?> wrappedBuilder, Set<Set<String>> attributesToCombine) {
        this(new TreeBuilder().maxDepth(3), wrappedBuilder, attributesToCombine);
    }

    public AttributeCombinerModelBuilder(TreeBuilder preBuilder, PredictiveModelBuilder<?> wrappedBuilder, Set<Set<String>> attributesToCombine) {
        this.preBuilder = preBuilder;
        this.wrappedBuilder = wrappedBuilder;
        this.attributesToCombine = Sets.newHashSet();
        for (Set<String> attributes : attributesToCombine) {
            this.attributesToCombine.add(Lists.newArrayList(attributes));
        }
    }

    @Override
    public AttributeCombinerPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
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

        final AttributeEnricher attributeEnricher = new AttributeEnricher(attributePreprocessors);
        final Iterable<Instance> enrichedTrainingData = Lists.newLinkedList(Iterables.transform(trainingData, new InstanceModifier(attributeEnricher)));

        logger.info("Building main predictive model");
        final PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(enrichedTrainingData);

        return new AttributeCombinerPredictiveModel(predictiveModel, attributeEnricher);
    }

}
