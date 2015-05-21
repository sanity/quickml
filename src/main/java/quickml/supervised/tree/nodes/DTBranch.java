package quickml.supervised.tree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.ClassificationCounter;

import java.io.IOException;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/28/15.
 */
public class DTBranch extends Branch<ClassificationCounter> implements DTNode {
    private DTNode trueChild, falseChild;

    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attributesToIgnore) {
        //TODO[mk] - check with Alex
        if (attributesToIgnore.contains(this.attribute)) {
            return probabilityOfTrueChild * trueChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore) +
                    (1 - probabilityOfTrueChild) * falseChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
        } else {
            if (decide(attributes)) {
                return trueChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            }
            else {
                return falseChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            }
        }
    }

    @Override
    public void dump(final int indent, final Appendable ap) {
        try {
            for (int x = 0; x < indent; x++) {
                ap.append(' ');
            }
            ap.append(this+"\n");
            trueChild.dump(indent + 2, ap);
            for (int x = 0; x < indent; x++) {
                ap.append(' ');
            }
            ap.append(toNotString() +"\n");
            falseChild.dump(indent + 2, ap);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
