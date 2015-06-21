package quickml.supervised.tree.decisionTree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Node;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public interface DTNode extends Node<ClassificationCounter, DTNode> {
    public abstract double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attribute);
    public abstract DTLeaf getLeaf(AttributesMap attributes);
}
