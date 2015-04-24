package quickml.supervised.classifier.tree.decisionTree.tree;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class TermStatistics {

    public Object attrVal;

    public TermStatistics(Object attrVal) {
        this.attrVal = attrVal;
    }

    public TermStatistics(){}

    public Object getAttrVal() {
        return attrVal;
    }

    public abstract double getTotal();

    public abstract boolean isEmpty();


}
