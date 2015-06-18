package quickml.supervised.tree.nodes;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.decisionTree.DTLeaf;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public interface DTNode extends Node<ClassificationCounter, DTNode> {
    public abstract double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attribute);
    public abstract DTLeaf getLeaf(AttributesMap attributes);
}
