package quickdt;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

public class BaggedTree {
    
    private final TreeBuilder treeBuilder;
    private final int numTrees;
    private final List<Node> trees;
    
    public BaggedTree(TreeBuilder treeBuilder, int numTrees) {
        this.trees = Lists.newArrayList();
        this.treeBuilder = treeBuilder;
        this.numTrees = numTrees;
    }
    
    public void build(Iterable<Instance> trainingData) {
        for (int i = 0; i < numTrees; i++) {
            List<Instance> sampling = getBootstrapSampling(trainingData);
            Node node = treeBuilder.buildTree(sampling);
            trees.add(node);
        }
    }
    
    public BaggingResult predict(Attributes attributes) {
        Multiset<Serializable> results = HashMultiset.create();
        for (Node tree : trees) {
            Leaf leaf = tree.getLeaf(attributes);
            results.add(leaf.classification);
        }
        
        Serializable winner = null;
        int winnerCount = 0;
        for (Serializable result : results.elementSet()) {
            int count = results.count(result);
            if (count > winnerCount) {
                winnerCount = count;
                winner = result;
            }
        }
        if (winner == null) {
            return null;
        }
        return new BaggingResult(winner, (double)winnerCount / results.size());
    }

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
