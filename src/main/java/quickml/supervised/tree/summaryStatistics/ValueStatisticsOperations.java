package quickml.supervised.tree.summaryStatistics;


/**
 * Created by alexanderhawk on 4/23/15.
 */
public interface ValueStatisticsOperations<TS extends ValueStatistics> {
    TS add(TS ts);
    TS subtract(TS ts);
}
