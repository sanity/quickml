package quickml.supervised.tree.decisionTree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.tree.BranchType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class BinaryClassifierDataProperties extends ClassifierDataProperties {

    final public Object minorityClassification;
    final public Object majorityClassification;
    final public double majorityToMinorityRatio;

    protected BinaryClassifierDataProperties(HashMap<Object, Long> classificationsAndCounts, Map<BranchType, Set<String>> candidateAttributesByBranchType, Object minorityClassification, Object majorityClassification, double majorityToMinorityRatio) {
        super(classificationsAndCounts, candidateAttributesByBranchType);
        this.minorityClassification = minorityClassification;
        this.majorityClassification = majorityClassification;
        this.majorityToMinorityRatio = majorityToMinorityRatio;
    }


    public static <T extends ClassifierInstance> ClassifierDataProperties createBinaryClassifierDataProperties(HashMap<Object, Long> classificationsAndCounts,
                                                                                                               Map<BranchType, Set<String>> candidateAttributesByBranchType) {
        Preconditions.checkArgument(classificationsAndCounts.keySet().size() ==2);
        Object minorityClassification = null;
        Object majorityClassification = null;
        boolean binaryClassifications = true;
        double majorityToMinorityRatio;
        double minorityClassificationCount = 0;

        double majorityClassificationCount = 0;
        for (Object val : classificationsAndCounts.keySet()) {
            if (majorityClassification == null || classificationsAndCounts.get(val).doubleValue() > majorityClassificationCount) {
                majorityClassification = val;
                majorityClassificationCount = classificationsAndCounts.get(val).doubleValue();
            }
            else if (minorityClassification == null || classificationsAndCounts.get(val).doubleValue() < minorityClassificationCount) {
                minorityClassification = val;
                minorityClassificationCount = classificationsAndCounts.get(val).doubleValue();
            }

        }
        majorityToMinorityRatio = classificationsAndCounts.get(majorityClassification).doubleValue()
                / classificationsAndCounts.get(minorityClassification).doubleValue();

        return new BinaryClassifierDataProperties(classificationsAndCounts, candidateAttributesByBranchType, minorityClassification, majorityClassification,majorityToMinorityRatio);
    }

    @Override
    public BinaryClassifierDataProperties copy(){
        //copy the map
        HashMap<Object, Long> classificationsAndCounts = Maps.newHashMap(this.classificationsAndCounts);
        return new BinaryClassifierDataProperties(classificationsAndCounts, super.candidateAttributesByBranchType, minorityClassification, majorityClassification, majorityToMinorityRatio);

    }


}

