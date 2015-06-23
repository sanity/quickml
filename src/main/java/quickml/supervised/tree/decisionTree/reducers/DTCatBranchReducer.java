package quickml.supervised.tree.decisionTree.reducers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.Reducer;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.List;
import java.util.Map;
import static quickml.supervised.tree.constants.MissingValue.*;


/**
 * Created by alexanderhawk on 4/23/15.
 */
public class DTCatBranchReducer<I extends ClassifierInstance> extends DTreeReducer<I> {

    @Override
    public AttributeStats<ClassificationCounter> getAttributeStats(String attribute) {
        Pair<ClassificationCounter, Map<Object, ClassificationCounter>> aggregateAndAttributeValueClassificationCounters = getAggregateAndAttributeValueClassificationCounters(attribute);
        ClassificationCounter aggregateStats = aggregateAndAttributeValueClassificationCounters.getValue0();
        Map<Object, ClassificationCounter> result = aggregateAndAttributeValueClassificationCounters.getValue1();
        List<ClassificationCounter> attributesWithClassificationCounters = Lists.newArrayList(result.values());
        return new AttributeStats<ClassificationCounter>(attributesWithClassificationCounters, aggregateStats, attribute);
    }


    protected Pair<ClassificationCounter, Map<Object, ClassificationCounter>> getAggregateAndAttributeValueClassificationCounters(String attribute) {
        final Map<Object, ClassificationCounter> result = Maps.newHashMap();
        final ClassificationCounter totals = new ClassificationCounter();
        for (ClassifierInstance instance : trainingData) {
            final Object attrVal = instance.getAttributes().get(attribute);
            ClassificationCounter cc;
            boolean acceptableMissingValue = attrVal == null;

            if (attrVal != null)
                cc = result.get(attrVal);
            else if (acceptableMissingValue)
                cc = result.get(MISSING_VALUE);
            else
                continue;

            if (cc == null) {
                cc = new ClassificationCounter(attrVal);
                Object newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
                result.put(newKey, cc);
            }
            cc.addClassification(instance.getLabel(), instance.getWeight());
            totals.addClassification(instance.getLabel(), instance.getWeight());
        }

        return Pair.with(totals, result);
    }

    @Override
    public void updateBuilderConfig(Map<String, Object> cfg) {

    }
}
