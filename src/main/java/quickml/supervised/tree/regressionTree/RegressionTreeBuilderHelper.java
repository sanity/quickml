
package quickml.supervised.tree.regressionTree;

import org.javatuples.Pair;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContextBuilder;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContext;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.regressionTree.treeBuildContexts.RTreeContext;
import quickml.supervised.tree.regressionTree.treeBuildContexts.RTreeContextBuilder;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class RegressionTreeBuilderHelper<I extends RegressionInstance> extends TreeBuilderHelper<I, MeanValueCounter> {

    RTreeContextBuilder<I> treeBuildContext;
    public RegressionTreeBuilderHelper(RTreeContextBuilder<I> treeBuildContext) {
        super(treeBuildContext);
        this.treeBuildContext = treeBuildContext;
    }

    public Node<MeanValueCounter> computeNodes(List<I> trainingData) {
        RTreeContext<I> itbc = treeBuildContext.buildContext(trainingData);
        Node<MeanValueCounter> root =  createNode(null, trainingData, itbc);
        return root;
    }

}

