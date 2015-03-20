package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import quickml.data.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchBuilder<T extends InstanceWithAttributesMap> {
    /**categorical, numeric, boolean*/
    private ImmutableList<String> attributesToConsider;
    public void setAttributesToConsider(ImmutableList<String> attributesToConsider){
        this.attributesToConsider = attributesToConsider;
    }
    public BranchType type;
    public abstract Optional<? extends Branch> findBestBranch(Branch parent, List<T> trainingData);
    public abstract BranchBuilder<T> copy();

}
