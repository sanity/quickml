package quickml.supervised.tree.decisionTree.reducers;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class DTBinaryCatBranchReducer<I extends ClassifierInstance> extends DTCatBranchReducer<I> {
    final Serializable minorityClassification;

    public DTBinaryCatBranchReducer(List<I> trainingData, Serializable minorityClassification) {
        super(trainingData);
        this.minorityClassification = minorityClassification;
    }

    @Override
    public Optional<AttributeStats<ClassificationCounter>> getAttributeStats(String attribute) {
        Optional<AttributeStats<ClassificationCounter>> attributeStatsOptional = super.getAttributeStats(attribute);
        if (!attributeStatsOptional.isPresent()) {
            return Optional.absent();
        }
        AttributeStats<ClassificationCounter> attributeStats = attributeStatsOptional.get();
        List<ClassificationCounter> attributesWithClassificationCounters = attributeStats.getStatsOnEachValue();
        Collections.sort(attributesWithClassificationCounters, new Comparator<ClassificationCounter>() {
            @Override
            public int compare(ClassificationCounter cc1, ClassificationCounter cc2) {
                double probOfMinority1 = cc1.getCount(minorityClassification) / cc1.getTotal();
                double probOfMinority2 = cc2.getCount(minorityClassification) / cc2.getTotal();
                return Ordering.natural().reverse().compare(probOfMinority1, probOfMinority2);
            }
        });
        return Optional.of(attributeStats);
    }
}
