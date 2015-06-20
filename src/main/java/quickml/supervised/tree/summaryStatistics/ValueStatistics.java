package quickml.supervised.tree.summaryStatistics;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class ValueStatistics {

    public Object attrVal;

    public ValueStatistics(Object attrVal) {
        this.attrVal = attrVal;
    }

    public ValueStatistics(){}

    public Object getAttrVal() {
        return attrVal;
    }

    public abstract double getTotal();

    public abstract boolean isEmpty();


}
