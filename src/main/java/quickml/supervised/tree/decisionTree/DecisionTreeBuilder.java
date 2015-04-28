package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.ClassifierDataProperties;
import quickml.supervised.tree.TreeBuilder;
import quickml.supervised.tree.configurations.TreeConfig;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilder<I extends ClassifierInstance> extends TreeBuilder<Object, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> {

    @Override
    protected Map<BranchType, InstancesToAttributeStatistics<Object, I, ClassificationCounter>> initializeInstancesToAttributeStatistics(InitializedTreeConfig<ClassificationCounter, ClassifierDataProperties> initializedTreeConfig) {
        //In general the instancesToAttributeStats may depend on training data, hence we put them here to hava access to the the initializad configurations.
        Map<BranchType, InstancesToAttributeStatistics<Object, I, ClassificationCounter>> instancesToAttributeStatisticsMap = Maps.newHashMap();
        //TODO: these entries should be linked to the branch finderBuilders some how...but not too strongly linked since decoupling is important to achieve parallelization, and use of inverted indices
        if (initializedTreeConfig.getDataProperties() instanceof  BinaryClassifierDataProperties) {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new InstanceToAttributeStatisticsForBinaryClassCatBranch<I>(((BinaryClassifierDataProperties) initializedTreeConfig.getDataProperties()).minorityClassification));
        } else {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new InstanceToAttributeStatisticsForCatBranch<I>());
        }
        instancesToAttributeStatisticsMap.put(BranchType.NUMERIC, new InstanceToAttributeStatisticsNumericBranch<I>());
        instancesToAttributeStatisticsMap.put(BranchType.BOOLEAN, new InstanceToAttributeStatisticsForCatBranch<I>());

        return  instancesToAttributeStatisticsMap;
    }

    //question, what should go in the configurations.  Should the user have to specify their branch Finder Builders. I don't know.
//mkae the input a decision configurations
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

