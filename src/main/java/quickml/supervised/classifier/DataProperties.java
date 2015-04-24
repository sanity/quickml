package quickml.supervised.classifier;

import com.google.common.collect.ImmutableList;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.BranchType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/24/15.
 */
public abstract class DataProperties {

    Map<BranchType, Set<String>> candidateAttributesByBranchType;

    public Map<BranchType, Set<String>> getCandidateAttributesByBranchType() {
        return candidateAttributesByBranchType;
    }

}
