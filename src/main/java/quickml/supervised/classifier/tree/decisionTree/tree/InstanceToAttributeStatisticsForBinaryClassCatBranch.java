package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class InstanceToAttributeStatisticsForBinaryClassCatBranch<I extends ClassifierInstance> extends InstanceToAttributeStatisticsForCatBranch<I>{
    //move to Binary ClassifierNodeBuilder.
    Object minorityClassification;

    public InstanceToAttributeStatisticsForBinaryClassCatBranch(Object minorityClassification) {
        this.minorityClassification = minorityClassification;
    }

    @Override
    public AttributeStats<ClassificationCounter> getAttributeStats(String attribute) {
        AttributeStats<ClassificationCounter> attributeStats = super.getAttributeStats(attribute);
        List<ClassificationCounter> attributesWithClassificationCounters = attributeStats.getTermStats();
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
