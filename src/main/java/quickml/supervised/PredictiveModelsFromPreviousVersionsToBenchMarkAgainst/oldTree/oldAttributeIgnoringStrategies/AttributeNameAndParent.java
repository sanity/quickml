package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies;

import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldBranch;

/**
 * Created by alexanderhawk on 3/2/15.
 */
public class AttributeNameAndParent {
    public final String attribute;
    public final OldBranch oldBranch;

    public AttributeNameAndParent(String attribute, OldBranch oldBranch) {
        this.attribute = attribute;
        this.oldBranch = oldBranch;
    }

}
