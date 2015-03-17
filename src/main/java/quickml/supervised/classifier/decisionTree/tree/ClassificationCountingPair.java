package quickml.supervised.classifier.decisionTree.tree;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by alexanderhawk on 3/13/15.
 */
public class ClassificationCountingPair {
    public HashMap<Serializable, Long> inCounter;
    public HashMap<Serializable, Long> outCounter;

    public ClassificationCountingPair(HashMap<Serializable, Long> inCounter, HashMap<Serializable, Long> outCounter) {
        this.inCounter = inCounter;
        this.outCounter = outCounter;
    }
}
