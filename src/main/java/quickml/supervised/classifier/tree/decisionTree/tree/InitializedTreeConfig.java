package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.base.Optional;
import quickml.supervised.classifier.tree.Tree;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class InitializedTreeConfig<GS extends TermStatistics, Tr extends Tree> {
//option here: if data properties only needed in service of intitializing the Attr. ignoring strategy, and the termination conditions, then only need d
    //in treeConfig...have a protected abstract method that initializes these objects we actually want to pass on, and in the constructor of the implementing class, we can initialize a field (the user doesn't
    //know about) we can create a data properties object that takes care of this for us.


    public InitializedTreeConfig(TerminationConditions<GS> terminationConditions, Scorer scorer, int numTrees,
                                 Iterable<BranchFinder<GS>> branchFinders, LeafBuilder<GS> leafBuilder, Optional<Bagging> bagging, TreeFactory<Tr> treeFactory

                                 ) {
        this.scorer = scorer;
        this.branchFinders = branchFinders;
        this.leafBuilder = leafBuilder;
        this.terminationConditions = terminationConditions;
        this.bagging = bagging;
        this.treeFactory = treeFactory;
    }

    private Scorer scorer;
    private TerminationConditions terminationConditions;
    private Optional<Bagging> bagging;
    private TreeFactory<Tr> treeFactory;
    private Iterable<BranchFinder<GS>> branchFinders;
    private LeafBuilder<GS> leafBuilder;



    public TreeFactory<Tr> getTreeFactory() { return treeFactory; }

    public LeafBuilder<GS> getLeafBuilder() {
        return leafBuilder;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public Iterable<BranchFinder<GS> >getBranchFinders(){
        return branchFinders;
    }


    public TerminationConditions getTerminationConditions() {
        return terminationConditions;
    }

    public Optional<Bagging> getBagging() {
        return bagging;
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
