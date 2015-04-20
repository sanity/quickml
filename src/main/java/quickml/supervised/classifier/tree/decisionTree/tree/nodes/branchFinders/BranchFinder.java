package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.List;

/**
 * Created by alexanderhawk on 3/24/15.
 */

public abstract class BranchFinder<GS extends TermStatistics> {

    public Optional<? extends Branch> findBestBranch(Branch parent, List<AttributeStats<GS>> groupStatsByAttribute) {
        double bestScore = 0;
        Optional<? extends Branch> bestBranchOptional = Optional.absent();

        for (AttributeStats<GS> attributeStats : groupStatsByAttribute) {

            Optional<? extends Branch> thisBranchOptional = getBranch(parent, attributeStats);
            if (thisBranchOptional.isPresent()) {
                Branch thisBranch = thisBranchOptional.get();
                if (thisBranch.score > bestScore) {
                    bestScore = thisBranch.score;
                    bestBranchOptional = thisBranchOptional;
                }
            }
        }
        return bestBranchOptional;
    }

    public abstract Optional<? extends Branch> getBranch(Branch parent, AttributeStats<GS> attributeStats);
}
