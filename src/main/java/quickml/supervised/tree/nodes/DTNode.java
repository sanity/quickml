package quickml.supervised.tree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.ClassificationCounter;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public interface DTNode extends Node<ClassificationCounter>{
    public abstract double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attribute);
    public void dump(final Appendable ap);
    public abstract void dump(int indent, Appendable ap);


}
