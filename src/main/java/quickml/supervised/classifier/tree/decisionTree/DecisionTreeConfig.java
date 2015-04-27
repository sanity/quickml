package quickml.supervised.classifier.tree.decisionTree;

import com.google.common.collect.Lists;
import quickml.supervised.classifier.ClassifierDataProperties;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.Bagging;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.NumericBranchFinderBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.TerminationConditions;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.BinaryCatBranchFinderBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.CategoricalBranchFinderBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;
import quickml.supervised.classifier.tree.treeConfig.TreeConfig;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public class DecisionTreeConfig extends TreeConfig<ClassificationCounter, ClassifierDataProperties> {


    public DecisionTreeConfig() {
        this.leafBuilder = new DTLeafBuilder();
        branchFinderBuilders = Lists.newArrayList();
        branchFinderBuilders.add(new NumericBranchFinderBuilder<ClassificationCounter, ClassifierDataProperties>());
        branchFinderBuilders.add(new CategoricalBranchFinderBuilder<ClassificationCounter, ClassifierDataProperties>());
        branchFinderBuilders.add(new BinaryCatBranchFinderBuilder<ClassificationCounter>());
    }

    @Override
    public DecisionTreeConfig bagging(Bagging bagging) {
        return this.bagging(bagging);
    }

    @Override
    public DecisionTreeConfig bagging(boolean bagging) {
        return this.bagging(bagging);
    }


    @Override
    public DecisionTreeConfig scorer(Scorer scorer) {
        return this.scorer(scorer);
    }

    @Override
    public DecisionTreeConfig branchFinderBuilders(BranchFinderBuilder<ClassificationCounter, ClassifierDataProperties>... branchFinderFactories) {
        return this.branchFinderBuilders(branchFinderFactories);
    }

    @Override
    public DecisionTreeConfig terminationConditions(TerminationConditions<ClassificationCounter> terminationConditions) {
        return this.terminationConditions(terminationConditions);
    }
}
