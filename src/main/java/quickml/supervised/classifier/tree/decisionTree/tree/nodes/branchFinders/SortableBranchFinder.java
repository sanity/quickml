package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.javatuples.Pair;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.AttributeValueData;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.MissingValue;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import static quickml.supervised.classifier.tree.decisionTree.tree.MissingValue.*;

/**
 * Created by alexanderhawk on 4/9/15.
 */
public abstract class SortableBranchFinder<T extends InstanceWithAttributesMap> extends BranchFinder<T> {

    protected SortableBranchFinder(ImmutableList<String> candidateAttributes, AttributeIgnoringStrategy attributeIgnoringStrategy){
        super(candidateAttributes, attributeIgnoringStrategy);
    }

    protected Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> countAllByAttributeValues(
            final Iterable<? extends InstanceWithAttributesMap> instances, final String attribute) {
        final Map<Serializable, ClassificationCounter> result = Maps.newHashMap();
        final ClassificationCounter totals = new ClassificationCounter();
        for (InstanceWithAttributesMap instance : instances) {
            final Serializable attrVal = instance.getAttributes().get(attribute);
            ClassificationCounter cc;
            boolean acceptableMissingValue = attrVal == null;

            if (attrVal != null)
                cc = result.get(attrVal);
            else if (acceptableMissingValue)
                cc = result.get(MISSING_VALUE);
            else
                continue;

            if (cc == null) {
                cc = new ClassificationCounter();
                Serializable newKey = (attrVal != null) ? attrVal : MISSING_VALUE;
                result.put(newKey, cc);
            }
            cc.addClassification(instance.getLabel(), instance.getWeight());
            totals.addClassification(instance.getLabel(), instance.getWeight());
        }

        return Pair.with(totals, result);
    }

    protected Pair<ClassificationCounter, List<AttributeValueData>> getSortedListOfAttributeValuesWithClassificationCounters(
            final Iterable<? extends InstanceWithAttributesMap> instances, final String attribute, final Serializable minorityClassification) {

        Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> totalsClassificationCounterPairedWithMapofClassificationCounters = countAllByAttributeValues(instances, attribute);
        final Map<Serializable, ClassificationCounter> result = totalsClassificationCounterPairedWithMapofClassificationCounters.getValue1();
        final ClassificationCounter totals = totalsClassificationCounterPairedWithMapofClassificationCounters.getValue0();

        List<AttributeValueData> attributesWithClassificationCounters = Lists.newArrayList();
        for (Serializable key : result.keySet()) {
            attributesWithClassificationCounters.add(new AttributeValueData(key, result.get(key)));
        }

        Collections.sort(attributesWithClassificationCounters, new Comparator<AttributeValueData>() {
            @Override
            public int compare(AttributeValueData cc1, AttributeValueData cc2) {
                double probOfMinority1 = cc1.classificationCounter.getCount(minorityClassification) / cc1.classificationCounter.getTotal();
                double probOfMinority2 = cc2.classificationCounter.getCount(minorityClassification) / cc2.classificationCounter.getTotal();

                return Ordering.natural().reverse().compare(probOfMinority1, probOfMinority2);
            }
        });

        return Pair.with(totals, attributesWithClassificationCounters);
    }

}
