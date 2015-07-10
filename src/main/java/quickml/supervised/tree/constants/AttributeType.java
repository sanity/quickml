package quickml.supervised.tree.constants;

import quickml.supervised.tree.nodes.Branch;

import javax.management.Attribute;

/**
 * Created by alexanderhawk on 6/24/15.
 */
public enum AttributeType {
    CATEGORICAL(), NUMERIC(), BOOLEAN();

    public static AttributeType convertBranchTypeToAttributeType(BranchType branchType) {
        if (branchType.name().equals(CATEGORICAL.name()) || branchType.equals(BranchType.BINARY_CATEGORICAL)) {
            return CATEGORICAL;
        } else if (branchType.name().equals(NUMERIC.name())) {
            return NUMERIC;
        } else if (branchType.name().equals(BOOLEAN.name())) {
            return BOOLEAN;
        } else {
            throw new RuntimeException("unknown branch type: " + branchType.name());
        }


    }
}
