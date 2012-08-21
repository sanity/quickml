package quickdt.bagging;

import java.io.Serializable;
import java.util.List;

import quickdt.Attributes;
import quickdt.Instance;
import quickdt.Leaf;
import quickdt.Misc;
import quickdt.Node;
import quickdt.TreeBuilder;

import com.google.common.base.Preconditions;
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
public class BaggedTree implements Serializable {

    private static final long serialVersionUID = 8996197519632788949L;

    /** Default number of trees to grow. */
    public static final int DEFAULT_TREE_COUNT = 10;

    private final List<Node> trees;

    private BaggedTree(List<Node> trees) {
	this.trees = trees;
    }

    /**
     * <p>
     * Factory method for creating a new {@link BaggedTree} with the specified
     * {@link TreeBuilder} and and the specified number of decision trees from
     * the provided training data.
     * </p>
     * 
     * @param treeBuilder
     *            The tree builder for building the decision trees, not
     *            <code>null</code>.
     * @param numTrees
     *            The number of trees to create, greater than zero.
     * @param training
     *            The training instances, not <code>null</code>.
     */
    public static BaggedTree build(TreeBuilder builder, int numTrees,
	    Iterable<Instance> training) {
	Preconditions.checkNotNull(builder);
	Preconditions.checkArgument(numTrees > 0,
		"numTrees must be greater than zero");
	Preconditions.checkNotNull(training);

	List<Node> trees = Lists.newArrayList();
	for (int i = 0; i < numTrees; i++) {
	    // System.out.println("building tree " + (i + 1) + " of " +
	    // numTrees);
	    List<Instance> sampling = getBootstrapSampling(training);
	    Node node = builder.buildTree(sampling);
	    trees.add(node);
	}
	return new BaggedTree(trees);
    }

    /**
     * <p>
     * Factory method for creating a new {@link BaggedTree} with the specified
     * {@link TreeBuilder} and {@value #DEFAULT_TREE_COUNT} decision trees from
     * the provided training data.
     * </p>
     * 
     * @param builder
     *            The tree builder for building the decision trees, not
     *            <code>null</code>.
     * @param training
     *            The training instances, not <code>null</code>.
     */
    public static BaggedTree build(TreeBuilder builder,
	    Iterable<Instance> training) {
	return build(builder, DEFAULT_TREE_COUNT, training);
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
	Preconditions.checkNotNull(attributes);

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
    private static List<Instance> getBootstrapSampling(
	    Iterable<Instance> trainingData) {
	List<Instance> allInstances = Lists.newArrayList(trainingData);
	List<Instance> sampling = Lists.newArrayList();
	for (int i = 0; i < allInstances.size(); i++) {
	    int sample = Misc.random.nextInt(allInstances.size());
	    sampling.add(allInstances.get(sample));
	}
	return sampling;
    }

    /**
     * <p>
     * Get a {@link List} of all trees.
     * </p>
     * 
     * @return
     */
    public List<Node> getTrees() {
	return trees;
    }

}
