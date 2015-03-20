package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.javatuples.Pair;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.ClassificationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public class ClassifierBestBranchFinder<T extends InstanceWithAttributesMap> extends BestBranchFinder<T>{
    ClassificationProperties classificationProperties;
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
        classificationProperties = ClassificationProperties.getClassificationProperties(instances);
        TrainingDataSurveyor<T> trainingDataSurveyor = new TrainingDataSurveyor<T>(considerBooleanAttributes);
        attributesByType = trainingDataSurveyor.groupAttributesByType(instances);
        for (BranchType branchType : branchBuilders.keySet()) {
            BranchBuilder<T> branchBuilder = branchBuilders.get(branchType);
            branchBuilder.setAttributesToConsider(attributesByType.get(branchType));
        }

    }

    @Override
    public ClassifierBestBranchFinder copy() {
        Map<BranchType, BranchBuilder> copiedBranchBuilders = new HashMap<>();
        for (BranchType key : copiedBranchBuilders.keySet()) {
            copiedBranchBuilders.put(key, this.branchBuilders.get(key).copy());
        }
        ClassifierBestBranchFinder<T> copy = new ClassifierBestBranchFinder<T>(copiedBranchBuilders, considerBooleanAttributes);
        copy.classificationProperties = classificationProperties;
        copy.attributesByType = attributesByType;
        return copy;
    }

    @Override
    public Optional<? extends Branch> findBestBranch(Branch parent, List<T> instances) {
        double bestScore = 0;
        Optional<? extends Branch> bestBranchOptional = Optional.absent();
        for (BranchType branchType : branchBuilders.keySet()) {
            BranchBuilder<T> branchBuilder =  branchBuilders.get(branchType);
            Optional<? extends Branch> thisBranchOptional = branchBuilder.findBestBranch(parent, instances);
            if (thisBranchOptional.isPresent()) {
                Branch thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {  //minScore evaluation delegated to branchBuilder
                    bestBranchOptional = thisBranchOptional;
                    bestScore = thisBranch.score;
                }
            }
        }
        return bestBranchOptional;
    }

}
