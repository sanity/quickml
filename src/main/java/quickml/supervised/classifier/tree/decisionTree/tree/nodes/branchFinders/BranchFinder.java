package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.DataForTheAssessmentOfSplitValidity;
import quickml.supervised.classifier.tree.decisionTree.tree.StandardTerminationConditions;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.CategoricalBranch;

import java.util.List;

/**
 * Created by alexanderhawk on 3/24/15.
 */

public abstract class BranchFinder<L, T extends InstanceWithAttributesMap<L>> {
    protected final ImmutableList<String> candidateAttributes;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;


    public BranchFinder(ImmutableList<String> candidateAttributes,AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.candidateAttributes = candidateAttributes;
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
    }

    public Optional<? extends Branch> findBestBranch(Branch parent, List<T> trainingData) {
        double bestScore = 0;
        Optional<? extends Branch> bestBranchOptional = Optional.absent();

        for (String attribute : candidateAttributes) {
            if (attributeIgnoringStrategy.ignoreAttribute(attribute,parent)) {
                continue;
            }
            Optional<? extends Branch> thisBranchOptional = getBranch(parent, trainingData, attribute);
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

    public abstract Optional<? extends Branch> getBranch(Branch parent, List<T> trainingData, String attribute);
}
