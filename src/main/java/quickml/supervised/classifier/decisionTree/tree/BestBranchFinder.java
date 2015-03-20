package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
//consider making an interface if possible

//simplest interface that accomodates TreeBuilder
public abstract class BestBranchFinder<T extends InstanceWithAttributesMap>  {

    Map<BranchType, BranchBuilder> branchBuilders;

    public BestBranchFinder(Map<BranchType, BranchBuilder> branchBuilders) {
        this.branchBuilders = branchBuilders;
    }

    public abstract void surveyTheData(List<T> instances);
    public abstract Optional<? extends Branch> findBestBranch(Branch Parent, List<T> instances);
    public abstract BestBranchFinder copy();

}