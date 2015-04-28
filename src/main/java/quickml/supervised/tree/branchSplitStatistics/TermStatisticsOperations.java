package quickml.supervised.tree.branchSplitStatistics;


/**
 * Created by alexanderhawk on 4/23/15.
 */
public interface TermStatisticsOperations<TS extends TermStatistics> {
    TS add(TS ts);
    TS subtract(TS ts);
}
