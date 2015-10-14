package quickml.supervised.tree.dataProcessing;

import java.util.HashSet;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class BinaryAttributeCharacteristics {
    boolean isBinary = true;
    private HashSet<Double> observedVals = new HashSet();

    public void updateBinaryStatus(double val) {
        if (isBinary) {
            observedVals.add(val);
            if (observedVals.size() > 1) {
                isBinary = false;
            }
        }
    }
}
