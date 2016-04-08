package quickml.supervised.tree.regressionTree.reducers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.javatuples.Pair;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static quickml.supervised.tree.constants.MissingValue.MISSING_VALUE;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class RTCatBranchReducer<I extends RegressionInstance> extends RTreeReducer<I> {

    public RTCatBranchReducer(List<I> trainingData) {
        super(trainingData);
    }

    @Override
    public Optional<AttributeStats<MeanValueCounter>> getAttributeStats(String attribute) {
        Optional<AttributeStats<MeanValueCounter>> attributeStatsOptional = getUnsortedAttributeStats(attribute);
        if (!attributeStatsOptional.isPresent()) {
            return Optional.absent();
        }
        AttributeStats<MeanValueCounter> attributeStats = attributeStatsOptional.get();
        List<MeanValueCounter> attributesWithClassificationCounters = attributeStats.getStatsOnEachValue();
        Collections.sort(attributesWithClassificationCounters, new Comparator<MeanValueCounter>() {
            @Override
            public int compare(MeanValueCounter mv1, MeanValueCounter mv2) {
                double meanOfOne = mv1.getAccumulatedValue() / mv1.getTotal();
                double meanOfTwo = mv2.getAccumulatedValue() / mv2.getTotal();
                return Ordering.natural().reverse().compare(meanOfOne, meanOfTwo);
            }
        });
        return Optional.of(attributeStats);
    }


    private Optional<AttributeStats<MeanValueCounter>> getUnsortedAttributeStats(String attribute) {
        Pair<MeanValueCounter, Map<Serializable, MeanValueCounter>> aggregateAndAttributeValueMeanValueCounters = getAggregateAndAttributeValueMeanValueCounters(attribute);
        MeanValueCounter aggregateStats = aggregateAndAttributeValueMeanValueCounters.getValue0();
        Map<Serializable, MeanValueCounter> result = aggregateAndAttributeValueMeanValueCounters.getValue1();
        List<MeanValueCounter> attributesWithMeanValueCounters= Lists.newArrayList(result.values());
        if (attributesWithMeanValueCounters.size() <=1) {
            return Optional.absent();
        }
        return  Optional.of(new AttributeStats<>(attributesWithMeanValueCounters, aggregateStats, attribute));
    }



    protected Pair<MeanValueCounter, Map<Serializable, MeanValueCounter>> getAggregateAndAttributeValueMeanValueCounters(String attribute) {
        final Map<Serializable, MeanValueCounter> result = Maps.newHashMap();
        final MeanValueCounter totals = new MeanValueCounter();
        for (RegressionInstance instance : getTrainingData()) {
            final Serializable attrVal = instance.getAttributes().get(attribute);
            MeanValueCounter mv;
            boolean acceptableMissingValue = attrVal == null; //|| attrVal.equals("");//trial

            if (attrVal != null)
                mv = result.get(attrVal);
            else if (acceptableMissingValue)
                mv = result.get(MISSING_VALUE);
            else
                continue;

            if (mv == null) {
                mv = new MeanValueCounter(attrVal != null ? attrVal : MISSING_VALUE);
                Serializable newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
                result.put(newKey, mv);
            }
            mv.update(instance.getLabel(), instance.getWeight());
            totals.update(instance.getLabel(), instance.getWeight());
        }

        return Pair.with(totals, result);
    }
}
