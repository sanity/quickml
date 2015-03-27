package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.DataProperties;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<T extends InstanceWithAttributesMap> {
    private ImmutableList<String> candidateSplitAttributes;

    public  BranchFinderBuilder<T> candidateSplitAttributes(Collection<String> candidateAttributesForSplits){
        this.candidateSplitAttributes = ImmutableList.<String>builder().addAll(candidateAttributesForSplits).build();
        return this;
    }

    public String branchName;
    public abstract  BranchFinderBuilder<T> copy();
    public abstract void update(Map<String, Object> cfg);
    public abstract BranchFinder<T> buildBranchFinder(DataProperties dataProperties);

}

