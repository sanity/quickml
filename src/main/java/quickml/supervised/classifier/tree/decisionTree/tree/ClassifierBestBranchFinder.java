package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.ClassifierDataProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public class ClassifierBestBranchFinder<T extends InstanceWithAttributesMap> extends BestBranchFinder<T>{
    ClassifierDataProperties classifierDataProperties;
    ImmutableMap<BranchType, ImmutableList<String>> attributesByType;
    boolean considerBooleanAttributes = false;

    public ClassifierBestBranchFinder(Map<BranchType, BranchBuilder> branchBuilders) {
        super(branchBuilders);
    }

    public ClassifierBestBranchFinder(Map<BranchType, BranchBuilder> branchBuilders, boolean considerBooleanAttributes) {
        super(branchBuilders);
        this.considerBooleanAttributes = considerBooleanAttributes;
    }

    @Override
    public void surveyTheData(List<T> instances) {
        classifierDataProperties = ClassifierDataProperties.createClassifierDataProperties(instances);
        TrainingDataSurveyor<T> trainingDataSurveyor = new TrainingDataSurveyor<T>(considerBooleanAttributes);
        attributesByType = trainingDataSurveyor.groupAttributesByType(instances);
        for (BranchType branchType : branchBuilders.keySet()) {
            BranchBuilder<T> branchBuilder = branchBuilders.get(branchType);
            branchBuilder.candidateSplitAttributes(attributesByType.get(branchType));
        }

        //create AttributeValueIgnoringStrategy

    }

    @Override
    public ClassifierBestBranchFinder copy() {
        Map<BranchType, BranchBuilder> copiedBranchBuilders = new HashMap<>();
        for (BranchType key : copiedBranchBuilders.keySet()) {
            copiedBranchBuilders.put(key, this.branchBuilders.get(key).copy());
        }
        ClassifierBestBranchFinder<T> copy = new ClassifierBestBranchFinder<T>(copiedBranchBuilders, considerBooleanAttributes);
        copy.classifierDataProperties = classifierDataProperties;
        copy.attributesByType = attributesByType;
        return copy;
    }


    @Override


}
