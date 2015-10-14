package quickml.supervised.tree.dataProcessing;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class AttributeCharacteristics {

    public boolean isNumber = true;
    public boolean isBoolean = true;
    private HashSet<Serializable> observedVals = new HashSet();

    public void updateBooleanStatus(Serializable val) {
        if (!isBoolean || val == null) {
            return;
        }
        if (observedVals.size()>2 || (observedVals.size() == 2 && !observedVals.contains(val))) {
            isBoolean = false;
        } else {
            observedVals.add(val);
        }
        if (bothValsAreNumbers()) {
            isBoolean = false;
        }
    }

    private boolean bothValsAreNumbers() {
        boolean bothValsAreNum = true;

        for (Serializable key: observedVals) {
            if (!(key instanceof Number))
                return false;
        }
        return true;
    }
}