package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.decisionTree.Scorer;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class ForestConfig<T extends InstanceWithAttributesMap> {

    public ForestConfig(Scorer scorer, double minScore, int minLeafInstances, int numTrees, int maxDepth, Iterable<BranchBuilder<T>> branchBuilders, LeafBuilder<T> leafBuilder) {
        this.scorer = scorer;
        this.minScore = minScore;
        this.minLeafInstances = minLeafInstances;
        this.numTrees = numTrees;
        this.maxDepth = maxDepth;
        this.branchBuilders = branchBuilders;
        this.leafBuilder = leafBuilder;
    }

    private Scorer scorer;
    private double minScore=0;
    private int minLeafInstances = 0;
    private int numTrees = 1;
    private int maxDepth = Integer.MAX_VALUE;
    private Iterable<BranchBuilder<T>> branchBuilders;
    private LeafBuilder<T> leafBuilder;


    public LeafBuilder<T> getLeafBuilder() {
        return leafBuilder;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public double getMinScore() {
        return minScore;
    }

    public int getMinLeafInstances() {
        return minLeafInstances;
    }

    public int getNumTrees() {
        return numTrees;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Iterator<? extends BranchBuilder<T>> getBranchBuilders(){
        return branchBuilders.iterator();
    }
/*


    //put in specific branch builders
    private int attributeValueObservationsThreshold = 0;
    private double degreeOfGainRatioPenalty = 1.0;
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5;
    private int samplesPerBin = 10;
    private boolean buildClassificationTrees = false;

    private ForestConfig(Map<ForestOptions, Object> cfg) {
        update(cfg);
    }
    */
    
}
