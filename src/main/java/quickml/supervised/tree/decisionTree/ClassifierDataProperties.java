package quickml.supervised.tree.decisionTree;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.tree.BranchType;
import quickml.supervised.tree.decisionTree.tree.TrainingDataSurveyor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class ClassifierDataProperties extends DataProperties{

    final ImmutableMap<Object, Long> classificationsAndCounts;
    protected ClassifierDataProperties(HashMap<Object, Long> classificationsAndCounts,
                                       Map<BranchType, Set<String>> candidateAttributesByBranchType) {
        super.candidateAttributesByBranchType = candidateAttributesByBranchType;
        this.classificationsAndCounts = new ImmutableMap.Builder<Object, Long>()
                .putAll(classificationsAndCounts)
                .build();
    }

    public static <I extends ClassifierInstance> ClassifierDataProperties createClassifierDataProperties(List<I> trainingData, boolean considerBooleanAttributes) {
        TrainingDataSurveyor<I> trainingDataSurveyor = new TrainingDataSurveyor<>(considerBooleanAttributes);
        Map<BranchType, Set<String>> candidateAttributesByBranchType = trainingDataSurveyor.groupAttributesByType(trainingData);
        HashMap<Object, Long> classificationsAndCounts = getClassificationsAndCounts(trainingData);
        if (classificationsAndCounts.keySet().size() > 2) {
            return new ClassifierDataProperties(classificationsAndCounts, candidateAttributesByBranchType);
        }
        return BinaryClassifierDataProperties.createBinaryClassifierDataProperties(classificationsAndCounts, candidateAttributesByBranchType);
    }

    private static <I extends ClassifierInstance> HashMap<Object, Long> getClassificationsAndCounts(List<I> trainingData) {
        HashMap<Object, Long> classificationsAndCounts = Maps.newHashMap();
        for (I instance : trainingData) {
            Object classification = instance.getLabel();

            if (classificationsAndCounts.containsKey(classification)) {
                classificationsAndCounts.put(classification, classificationsAndCounts.get(classification) + 1L);

            } else
                classificationsAndCounts.put(classification, 1L);

        }
        return classificationsAndCounts;
    }

    public Set<Object> getClassifications() {
        return Sets.newHashSet(classificationsAndCounts.keySet());
    }

    public boolean classificationsAreBinary() {
        return classificationsAndCounts.size() == 2;
    }

    public ImmutableMap<Object, Long> getClassificationsAndCounts() {
        return classificationsAndCounts;
    }

    public ClassifierDataProperties copy() {
        HashMap<Object, Long> classificationsAndCounts = Maps.newHashMap(this.classificationsAndCounts);
        return new ClassifierDataProperties(classificationsAndCounts, candidateAttributesByBranchType);

    }

}

