
package quickml.supervised.tree.decisionTree;

import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContextBuilder;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContext;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Node;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class DecisionTreeBuilderHelper<I extends ClassifierInstance> extends TreeBuilderHelper<I, ClassificationCounter> {

    DTreeContextBuilder<I> treeBuildContext;
    public DecisionTreeBuilderHelper(DTreeContextBuilder<I> treeBuildContext) {
        super(treeBuildContext);
        this.treeBuildContext = treeBuildContext;
    }

    public Pair<Node<ClassificationCounter>, Set<Serializable>> computeNodesAndClasses(List<I> trainingData) {
        DTreeContext<I> itbc = treeBuildContext.buildContext(trainingData);
        Node<ClassificationCounter> root =  createNode(null, trainingData, itbc);
        return Pair.with(root, itbc.getClassifications());
    }

}

