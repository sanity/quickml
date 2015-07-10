package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies;

import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldBranch;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public interface AttributeIgnoringStrategy extends Serializable {

    /**
     * Should this attribute be ignored
     * @param attribute
     * @param parent
     * @return
     */
    boolean ignoreAttribute(String attribute, OldBranch parent);

    /**
     * @return a copy of this AttributeIgnoringStrategy
     */
    AttributeIgnoringStrategy copy();
}
