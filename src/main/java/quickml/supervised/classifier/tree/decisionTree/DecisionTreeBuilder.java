package quickml.supervised.classifier.tree.decisionTree;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.BinaryClassifierDataProperties;
import quickml.supervised.classifier.ClassifierDataProperties;
import quickml.supervised.classifier.tree.DecisionTree;
import quickml.supervised.classifier.tree.TreeBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.treeConfig.TreeConfig;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilder<I extends ClassifierInstance> extends TreeBuilder<Object, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> {

    @Override
    protected Map<BranchType, InstancesToAttributeStatistics<Object, I, ClassificationCounter>> initializeInstancesToAttributeStatistics(InitializedTreeConfig<ClassificationCounter, ClassifierDataProperties> initializedTreeConfig) {
        //In general the instancesToAttributeStats may depend on training data, hence we put them here to hava access to the the initializad treeConfig.
        Map<BranchType, InstancesToAttributeStatistics<Object, I, ClassificationCounter>> instancesToAttributeStatisticsMap = Maps.newHashMap();
        //TODO: these entries should be linked to the branch finderBuilders some how...but not too strongly linked since decoupling is important to achieve parallelization, and use of inverted indices
        if (initializedTreeConfig.getDataProperties() instanceof  BinaryClassifierDataProperties) {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new InstanceToAttributeStatisticsForBinaryClassCatBranch<I>(((BinaryClassifierDataProperties) initializedTreeConfig.getDataProperties()).minorityClassification));
        } else {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new InstanceToAttributeStatisticsForCatBranch<I>());
        }
        instancesToAttributeStatisticsMap.put(BranchType.NUMERIC, new InstanceToAttributeStatisticsNumericBranch<I>());
        return  instancesToAttributeStatisticsMap;
    }

    public DecisionTreeBuilder(TreeConfig<ClassificationCounter, ClassifierDataProperties> treeConfig) {
        super(treeConfig, new DecisionTreeConfigInitializer(), new AggregateClassificationCounts<I>());
    }

    @Override
    public TreeBuilder<Object, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> copy() {
        return new DecisionTreeBuilder<>(treeConfig);
    }

    @Override
    protected DecisionTree constructTree(Node node, ClassifierDataProperties dataProperties) {
        return new DecisionTree(node, dataProperties.getClassifications());
    }

 }

