package quickml.supervised.classifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.BranchType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class AttributeAndBinaryClassificationProperties<T extends InstanceWithAttributesMap> extends AttributeAndClassificationProperties<T> {

    final public Serializable minorityClassification;
    final public Serializable majorityClassification;
    final public double majorityToMinorityRatio;

    protected AttributeAndBinaryClassificationProperties(List<T> trainingData, HashMap<Serializable, Long> classificationsAndCounts, Map<BranchType, ImmutableList<String>> candidateAttributesByBranchType, Serializable minorityClassification, Serializable majorityClassification, double majorityToMinorityRatio) {
        super(trainingData, classificationsAndCounts, candidateAttributesByBranchType);
        this.minorityClassification = minorityClassification;
        this.majorityClassification = majorityClassification;
        this.majorityToMinorityRatio = majorityToMinorityRatio;
    }


    protected static <T extends InstanceWithAttributesMap> AttributeAndClassificationProperties createClassificationPropertiesOfBinaryData(List<T> trainingData, HashMap<Serializable, Long> classificationsAndCounts,
                                                                                                                                           Map<BranchType, ImmutableList<String>> candidateAttributesByBranchType) {
        Preconditions.checkArgument(classificationsAndCounts.keySet().size() ==2);
        Serializable minorityClassification = null;
        Serializable majorityClassification = null;
        boolean binaryClassifications = true;
        double majorityToMinorityRatio;
        double minorityClassificationCount = 0;

        double majorityClassificationCount = 0;
        for (Serializable val : classificationsAndCounts.keySet()) {
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

        return new AttributeAndBinaryClassificationProperties<T>(trainingData, classificationsAndCounts, candidateAttributesByBranchType, minorityClassification, majorityClassification,majorityToMinorityRatio);
    }

    @Override
    public AttributeAndBinaryClassificationProperties copy(){
        //copy the map
        HashMap<Serializable, Long> classificationsAndCounts = Maps.newHashMap(this.classificationsAndCounts);
        return new AttributeAndBinaryClassificationProperties(super.trainingData, classificationsAndCounts, super.candidateAttributesByBranchType, minorityClassification, majorityClassification, majorityToMinorityRatio);

    }


}

