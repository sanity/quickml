package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.collect.ImmutableList;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.DataProperties;

import java.util.Collection;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public interface BranchFinderBuilder<T extends InstanceWithAttributesMap, D extends DataProperties> {

    BranchFinderBuilder<T, D> copy();

    void update(Map<String, Object> cfg);

    BranchFinder<T> buildBranchFinder(D dataProperties);

}

