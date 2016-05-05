package quickml.supervised.dataProcessing;

import java.util.HashSet;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class BinaryAttributeCharacteristics {
    private boolean isBinary = true;
    private HashSet<Double> observedVals = new HashSet();

    public boolean getIsBinary() {
        return isBinary;
    }

    public void updateBinaryStatus(double val) {
        if (isBinary) {
            observedVals.add(val);
            if (observedVals.size() > 1) {
                isBinary = false;
            }
        }
    }
}
