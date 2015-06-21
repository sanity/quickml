
package quickml.supervised.tree.decisionTree;

import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContextBuilder;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContext;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.decisionTree.nodes.DTNode;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilderHelper<I extends ClassifierInstance> extends TreeBuilderHelper<Object, I, ClassificationCounter, DTNode> {

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

