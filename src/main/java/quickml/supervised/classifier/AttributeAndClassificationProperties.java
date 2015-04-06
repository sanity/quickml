package quickml.supervised.classifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.BranchType;
import quickml.supervised.classifier.tree.decisionTree.tree.TrainingDataSurveyor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class AttributeAndClassificationProperties<T extends InstanceWithAttributesMap> extends DataProperties<T>{

    final ImmutableMap<Serializable, Long> classificationsAndCounts;
    final Map<BranchType, ImmutableList<String>> candidateAttributesByBranchType;

    protected AttributeAndClassificationProperties(List<T> trainingData, HashMap<Serializable, Long> classificationsAndCounts,
                                                   Map<BranchType, ImmutableList<String>> candidateAttributesByBranchType) {
        super(trainingData);
        this.candidateAttributesByBranchType = candidateAttributesByBranchType;
        this.classificationsAndCounts = new ImmutableMap.Builder<Serializable, Long>()
                .putAll(classificationsAndCounts)
                .build();
    }

    public static <T extends InstanceWithAttributesMap> AttributeAndClassificationProperties setDataProperties(List<T> trainingData, boolean considerBooleanAttributes) {
        TrainingDataSurveyor<T> trainingDataSurveyor = new TrainingDataSurveyor<>(considerBooleanAttributes);
        Map<BranchType, ImmutableList<String>> candidateAttributesByBranchType = trainingDataSurveyor.groupAttributesByType(trainingData);
        HashMap<Serializable, Long> classificationsAndCounts = getClassificationsAndCounts(trainingData);
        if (classificationsAndCounts.keySet().size() > 2) {
            return new AttributeAndClassificationProperties<T>(trainingData, classificationsAndCounts, candidateAttributesByBranchType);
        }
        return AttributeAndBinaryClassificationProperties.createClassificationPropertiesOfBinaryData(trainingData, classificationsAndCounts, candidateAttributesByBranchType);
    }

    private static <T extends InstanceWithAttributesMap> HashMap<Serializable, Long> getClassificationsAndCounts(List<T> trainingData) {
        HashMap<Serializable, Long> classificationsAndCounts = Maps.newHashMap();
        for (T instance : trainingData) {
            Serializable classification = instance.getLabel();

            if (classificationsAndCounts.containsKey(classification)) {
                classificationsAndCounts.put(classification, classificationsAndCounts.get(classification) + 1L);

            } else
                classificationsAndCounts.put(classification, 1L);

        }
        return classificationsAndCounts;
    }

    public Set<Serializable> getClassifications() {
        return Sets.newHashSet(classificationsAndCounts.keySet());
    }

    public Map<BranchType, ImmutableList<String>> getCandidateAttributesByBranchType() {
        return candidateAttributesByBranchType;
    }

    public boolean classificationsAreBinary() {
        return classificationsAndCounts.size() == 2;
    }

    public ImmutableMap<Serializable, Long> getClassificationsAndCounts() {
        return classificationsAndCounts;
    }

    public AttributeAndClassificationProperties copy() {
        HashMap<Serializable, Long> classificationsAndCounts = Maps.newHashMap(this.classificationsAndCounts);
        return new AttributeAndClassificationProperties(super.trainingData, classificationsAndCounts, candidateAttributesByBranchType);

    }

}

