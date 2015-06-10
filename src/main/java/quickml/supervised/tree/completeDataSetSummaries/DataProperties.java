package quickml.supervised.tree.completeDataSetSummaries;

import quickml.supervised.tree.constants.BranchType;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/24/15.
 */
public class DataProperties {

    protected Map<BranchType, Set<String>> candidateAttributesByBranchType;

    public Map<BranchType, Set<String>> getCandidateAttributesOfAllBranchFinders() {
        return candidateAttributesByBranchType;
    }

    public Set<String> getCandidateAttributesForBranchType(BranchType branchType) {
        return candidateAttributesByBranchType.get(branchType);
    }

}
