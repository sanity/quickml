package quickml.supervised.classifier.tree.decisionTree.tree.nodes;

import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;

import java.util.List;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public class AttributeStats<TS extends TermStatistics> {
    List<TS> termStats;
    TS aggregateStats;
    String attribute;

    public List<TS> getTermStats() {
        return termStats;
    }
    public TS getAggregateStats() {
        /**
         * has all stats on all terms and training instances even with missing values.
         */
        return aggregateStats;
    }

    public String getAttribute() {
        return attribute;
    }
    //TODO: create children classes that get stats on crossVal set
}
