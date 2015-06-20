package quickml.supervised.tree.decisionTree;

import quickml.data.ClassifierInstance;
import quickml.supervised.tree.ClassifierDataProperties;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.configurations.TreeBuildContext;
import quickml.supervised.tree.configurations.TreeConfig;
import quickml.supervised.tree.configurations.TreeConfigInitializer;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/21/15.
 */
public class DecisionTreeConfigInitializer<I extends ClassifierInstance> extends TreeConfigInitializer<Object, I, ClassificationCounter, ClassifierDataProperties>{

    @Override
    protected ClassifierDataProperties getDataProperties(List<I> instances, Set<BranchType> branchTypes) {
        return ClassifierDataProperties.createClassifierDataProperties(instances, branchTypes.contains(BranchType.BOOLEAN));
    }

    @Override
    public TreeBuildContext<ClassificationCounter, ClassifierDataProperties> createTreeConfig(List<I> instances, TreeConfig<ClassificationCounter, ClassifierDataProperties> fcb) {
        return super.createTreeConfig(instances, fcb);
    }

    @Override
    public TreeConfigInitializer<Object, I, ClassificationCounter, ClassifierDataProperties> copy() {
        return new DecisionTreeConfigInitializer();
    }
}
