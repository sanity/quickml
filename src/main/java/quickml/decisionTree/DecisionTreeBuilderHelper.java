
package quickml.decisionTree;

import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.decisionTree.treeBuildContexts.DTreeContextBuilder;
import quickml.decisionTree.treeBuildContexts.DTreeContext;
import quickml.decisionTree.valueCounters.ClassificationCounter;
import quickml.decisionTree.nodes.DTNode;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilderHelper<I extends ClassifierInstance> extends TreeBuilderHelper<I, ClassificationCounter, DTNode> {

    DTreeContextBuilder<I> treeBuildContext;
    public DecisionTreeBuilderHelper(DTreeContextBuilder<I> treeBuildContext) {
        super(treeBuildContext);
        this.treeBuildContext = treeBuildContext;
    }

    public Pair<DTNode, Set<Object>> computeNodesAndClasses(List<I> trainingData) {
        DTreeContext<I> itbc = treeBuildContext.buildContext(trainingData);
        DTNode root =  createNode(null, trainingData, itbc);
        return Pair.with(root, itbc.getClassifications());
    }

}

