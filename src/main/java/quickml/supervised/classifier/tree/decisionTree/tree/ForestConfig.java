package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class ForestConfig<T extends InstanceWithAttributesMap, S extends SplitProperties, D extends DataProperties> {

    public ForestConfig(TerminationConditions<T, S> terminationConditions, Scorer scorer, int numTrees,
                        Iterable<BranchFinder<T>> branchFinders, LeafBuilder<T> leafBuilder, Optional<Bagging<T>> bagging,
                        Optional<Double> downSamplingTargetMinorityProportion, Optional<PostPruningStrategy<T>> postPruningStrategy,
                        TreeFactory<D> treeFactory, D dataProperities) {
        this.scorer = scorer;
        this.numTrees = numTrees;
        this.branchFinders = branchFinders;
        this.leafBuilder = leafBuilder;
        this.terminationConditions = terminationConditions;
        this.bagging = bagging;
        this.downSamplingTargetMinorityProportion = downSamplingTargetMinorityProportion;
        this.postPruningStrategy = postPruningStrategy;
        this.treeFactory = treeFactory;
        this.dataProperities = dataProperities;
    }

    private Scorer scorer;
    private int numTrees = 1;
    private TerminationConditions terminationConditions;
    private Optional<Bagging<T>> bagging;
    private Optional<Double> downSamplingTargetMinorityProportion;
    private Optional<PostPruningStrategy<T>> postPruningStrategy;
    private TreeFactory<?> treeFactory;
    private D dataProperities;
    private Iterable<BranchFinder<T>> branchFinders;
    private LeafBuilder<T> leafBuilder;

    public TreeFactory<?> getTreeFactory() {
        return treeFactory;
    }

    public D getDataProperities() {
        return dataProperities;
    }

    public LeafBuilder<T> getLeafBuilder() {
        return leafBuilder;
    }

    public Scorer getScorer() {
        return scorer;
    }


    public int getNumTrees() {
        return numTrees;
    }

    public Iterable<BranchFinder<T>> getBranchFinders(){
        return branchFinders;
    }


    public TerminationConditions getTerminationConditions() {
        return terminationConditions;
    }

    public Optional<Bagging<T>> getBagging() {
        return bagging;
    }

    public Optional<PostPruningStrategy<T>> getPostPruningStrategy() {
        return postPruningStrategy;
    }


    public Optional<Double> getDownSamplingTargetMinorityProportion() {
        return downSamplingTargetMinorityProportion;
    }
/*


    //put in specific branch builders
    private int attributeValueObservationsThreshold = 0;
    private double degreeOfGainRatioPenalty = 1.0;
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5;
    private int samplesPerBin = 10;

    private ForestConfig(Map<ForestOptions, Object> cfg) {
        update(cfg);
    }
    */
    
}
