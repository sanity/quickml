package quickml.supervised.tree.summaryStatistics;


import java.io.Serializable;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public abstract class ValueCounter<VC extends ValueStatistics> extends ValueStatistics implements ValueStatisticsOperations<VC> {
    public ValueCounter() {
        super();
    }
    public ValueCounter(Serializable attrVal) {
        super(attrVal);
    }
}
