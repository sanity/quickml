package quickml.supervised.tree.summaryStatistics;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class ValueStatistics {

    public Serializable attrVal;

    public ValueStatistics(Serializable attrVal) {
        this.attrVal = attrVal;
    }

    public ValueStatistics(){}

    public Serializable getAttrVal() {
        return attrVal;
    }

    public abstract double getTotal();

    public abstract boolean isEmpty();



}
