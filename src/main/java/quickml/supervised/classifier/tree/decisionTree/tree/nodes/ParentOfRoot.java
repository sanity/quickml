package quickml.supervised.classifier.tree.decisionTree.tree.nodes;

import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class ParentOfRoot<TS extends TermStatistics> extends Branch<TS> {
    public ParentOfRoot() {
        super(null, "parentOfRoot", 0, Scorer.NO_SCORE, null);
    }

    @Override
    public boolean decide(Map<String, Object> attributes) {
        return false;
    }

    @Override
    public String toNotString() {
        return null;
    }
}
