package quickdt.bagging;

import java.io.Serializable;
import java.util.List;

import quickdt.Attributes;
import quickdt.Instance;
import quickdt.Leaf;
import quickdt.Misc;
import quickdt.Node;
import quickdt.TreeBuilder;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

/**
 * <p>
 * Simple implementation of a bagging predictor using multiple decision trees.
 * The idea is to create a random bootstrap sample of the training data to grow
 * multiple trees. Prediction is done by letting all the trees vote and taking
 * the winner class. For more information see <a
 * href="http://www.stat.berkeley.edu/tech-reports/421.pdf">Bagging
 * Predictors</a>, Leo Breiman, 1994.
 * </p>
 * 
 * @author Philipp Katz
 */
public class BaggedTree {

    /** Default number of trees to grow. */
    public static final int DEFAULT_TREE_COUNT = 10;

    private final TreeBuilder treeBuilder;
    private final int numTrees;
    private final List<Node> trees;

    /**
     * <p>
     * Create a new {@link BaggedTree} with the specified {@link TreeBuilder}
     * and {@value #DEFAULT_TREE_COUNT} decision trees.
     * </p>
     * 
     * @param treeBuilder
     */
    public BaggedTree(TreeBuilder treeBuilder) {
	this(treeBuilder, DEFAULT_TREE_COUNT);
    }

    /**
     * <p>
     * Create a new {@link BaggedTree} with the specified {@link TreeBuilder}
     * and the specified number of decision trees.
     * </p>
     * 
     * @param treeBuilder
     * @param numTrees
     */
    public BaggedTree(TreeBuilder treeBuilder, int numTrees) {
	this.trees = Lists.newArrayList();
	this.treeBuilder = treeBuilder;
	this.numTrees = numTrees;
    }

    public void build(Iterable<Instance> trainingData) {
	for (int i = 0; i < numTrees; i++) {
	    // System.out.println("building tree " + (i + 1) + " of " +
	    // numTrees);
	    List<Instance> sampling = getBootstrapSampling(trainingData);
	    Node node = treeBuilder.buildTree(sampling);
	    trees.add(node);
	}
    }

    /**
     * <p>
     * Predict classification for the supplied attributes by letting all
     * decision trees vote.
     * </p>
     * 
     * @param attributes
     * @return
     */
    public BaggingResult predict(Attributes attributes) {
	Multiset<Serializable> results = HashMultiset.create();
	for (Node tree : trees) {
	    Leaf leaf = tree.getLeaf(attributes);
	    results.add(leaf.classification);
	}
	return new BaggingResult(results);
    }

    /**
     * <p>
     * Create bootstrap sampling of the training data, by drawing randomly with
     * replacement, i.e. one sample might be
     * </p>
     * 
     * @param trainingData
     * @return
     */
    private List<Instance> getBootstrapSampling(Iterable<Instance> trainingData) {
	List<Instance> allInstances = Lists.newArrayList(trainingData);
	List<Instance> sampling = Lists.newArrayList();
	for (int i = 0; i < allInstances.size(); i++) {
	    int sample = Misc.random.nextInt(allInstances.size());
	    sampling.add(allInstances.get(sample));
	}
	return sampling;
    }

}
