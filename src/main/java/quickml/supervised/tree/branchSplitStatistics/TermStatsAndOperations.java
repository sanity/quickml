package quickml.supervised.tree.branchSplitStatistics;


/**
 * Created by alexanderhawk on 4/23/15.
 */
public abstract class TermStatsAndOperations<TS extends TermStatistics> extends TermStatistics implements TermStatisticsOperations<TS> {
    public TermStatsAndOperations() {
        super();
    }
    public TermStatsAndOperations(Object attrVal) {
        super(attrVal);
    }

}
