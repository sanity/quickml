package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Ordering;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.nodes.AttributeStats;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class AttributeStatisticsForBinaryClassCatBranch<I extends ClassifierInstance> extends AttributeStatisticsForCatBranch<I> {
    //move to Binary ClassifierNodeBuilder.
    Object minorityClassification;

    public AttributeStatisticsForBinaryClassCatBranch(Object minorityClassification) {
        this.minorityClassification = minorityClassification;
    }

    @Override
    public AttributeStats<ClassificationCounter> getAttributeStats(String attribute) {
        AttributeStats<ClassificationCounter> attributeStats = super.getAttributeStats(attribute);
        List<ClassificationCounter> attributesWithClassificationCounters = attributeStats.getStatsOnEachValue();
        Collections.sort(attributesWithClassificationCounters, new Comparator<ClassificationCounter>() {
            @Override
            public int compare(ClassificationCounter cc1, ClassificationCounter cc2) {
                double probOfMinority1 = cc1.getCount(minorityClassification) / cc1.getTotal();
                double probOfMinority2 = cc2.getCount(minorityClassification) / cc2.getTotal();
                return Ordering.natural().reverse().compare(probOfMinority1, probOfMinority2);
            }
        });
        return attributeStats;
    }
}
