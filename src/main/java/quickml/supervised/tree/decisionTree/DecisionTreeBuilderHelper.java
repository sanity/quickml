
package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.data.PredictionMap;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.summaryStatistics.TrainingDataReducer;
import quickml.supervised.tree.configurations.TreeBuildContext;
import quickml.supervised.tree.configurations.TreeConfig;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.DTNode;
import quickml.supervised.tree.nodes.NodeBase;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilderHelper<I extends ClassifierInstance> extends TreeBuilderHelper<Object, PredictionMap, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> {

    @Override
    protected Map<BranchType, TrainingDataReducer<Object, I, ClassificationCounter>> initializeInstancesToAttributeStatistics(TreeBuildContext<ClassificationCounter, ClassifierDataProperties> treeBuildContext) {
        //In general the instancesToAttributeStats may depend on training data, hence we put them here to have access to the the initializad configurations.

        //alternative: each branchFinder get's initialized by a method that takes the training data

        Map<BranchType, TrainingDataReducer<Object, I, ClassificationCounter>> instancesToAttributeStatisticsMap = Maps.newHashMap();
        if (treeBuildContext.getDataProperties() instanceof  BinaryClassifierDataProperties) {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new AttributeStatisticsForBinaryClassCatBranch<I>(((BinaryClassifierDataProperties) treeBuildContext.getDataProperties()).minorityClassification));
        } else {
            instancesToAttributeStatisticsMap.put(BranchType.CATEGORICAL, new AttributeStatisticsForCatBranch<I>());
        }
        instancesToAttributeStatisticsMap.put(BranchType.NUMERIC, new AttributeStatisticsNumericBranch<I>());
        instancesToAttributeStatisticsMap.put(BranchType.BOOLEAN, new AttributeStatisticsForCatBranch<I>());

        return  instancesToAttributeStatisticsMap;
    }


    public DecisionTreeBuilderHelper(TreeConfig<ClassificationCounter, ClassifierDataProperties> treeConfig) {
        super(treeConfig, new DecisionTreeConfigInitializer(), new ClassificationCounterProducer<I>());
    }

    @Override
    public TreeBuilderHelper<Object, PredictionMap, I, ClassificationCounter, DecisionTree, ClassifierDataProperties> copy() {
        return new DecisionTreeBuilderHelper<>(treeConfig);
    }


    //TODO need better solution than casting
    @Override
    protected DecisionTree constructTree(NodeBase<ClassificationCounter> node, ClassifierDataProperties dataProperties) {
        return new DecisionTree((DTNode)node, dataProperties.getClassifications());
    }

 }

