package quickml.supervised.tree.decisionTree.reducers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import static quickml.supervised.tree.constants.MissingValue.*;


/**
 * Created by alexanderhawk on 4/23/15.
 */
public class DTCatBranchReducer<I extends ClassifierInstance> extends DTreeReducer<I> {
    public DTCatBranchReducer(List<I> trainingData) {
        super(trainingData);
    }

    @Override
    public Optional<AttributeStats<ClassificationCounter>> getAttributeStats(String attribute) {
        Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> aggregateAndAttributeValueClassificationCounters = getAggregateAndAttributeValueClassificationCounters(attribute);
        ClassificationCounter aggregateStats = aggregateAndAttributeValueClassificationCounters.getValue0();
        Map<Serializable, ClassificationCounter> result = aggregateAndAttributeValueClassificationCounters.getValue1();
        List<ClassificationCounter> attributesWithClassificationCounters = Lists.newArrayList(result.values());
        if (attributesWithClassificationCounters.size() <=1) {
            return Optional.absent();
        }
        return  Optional.of(new AttributeStats<>(attributesWithClassificationCounters, aggregateStats, attribute));
    }


    protected Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> getAggregateAndAttributeValueClassificationCounters(String attribute) {
        final Map<Serializable, ClassificationCounter> result = Maps.newHashMap();
        final ClassificationCounter totals = new ClassificationCounter();
        for (ClassifierInstance instance : getTrainingData()) {
            final Serializable attrVal = instance.getAttributes().get(attribute);
            ClassificationCounter cc;
            boolean acceptableMissingValue = attrVal == null; //|| attrVal.equals("");//trial

            if (attrVal != null)
                cc = result.get(attrVal);
            else if (acceptableMissingValue)
                cc = result.get(MISSING_VALUE);
            else
                continue;

            if (cc == null) {
                cc = new ClassificationCounter(attrVal != null ? attrVal : MISSING_VALUE);
                Serializable newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
                result.put(newKey, cc);
            }
            cc.addClassification(instance.getLabel(), instance.getWeight());
            totals.addClassification(instance.getLabel(), instance.getWeight());
        }

        return Pair.with(totals, result);
    }

}
