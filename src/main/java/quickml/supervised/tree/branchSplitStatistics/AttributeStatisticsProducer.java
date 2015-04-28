package quickml.supervised.tree.branchSplitStatistics;


/**
 * Created by alexanderhawk on 4/22/15.
 */
public interface AttributeStatisticsProducer<TS extends TermStatistics> {
    AttributeStats<TS> getAttributeStats(String attribute);
}
