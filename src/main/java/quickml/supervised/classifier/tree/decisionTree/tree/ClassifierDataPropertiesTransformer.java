package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;

import java.util.List;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public class ClassifierDataPropertiesTransformer<T extends InstanceWithAttributesMap, S extends SplitProperties> implements DataPropertiesTransformer<T, S, AttributeAndClassificationProperties<T>> {
    private boolean considerBooleanAttributes = false;

    public ClassifierDataPropertiesTransformer(boolean considerBooleanAttributes) {
        this.considerBooleanAttributes= considerBooleanAttributes;
    }

    @Override
    public ForestConfig<T, S, AttributeAndClassificationProperties<T>> createForestConfig(List<T> instances, ForestConfigBuilder<T, S, AttributeAndClassificationProperties<T>> fcb) {
        AttributeAndClassificationProperties attributeAndClassificationProperties = AttributeAndClassificationProperties.setDataProperties(instances, considerBooleanAttributes);
        List<BranchFinder<T>> initializedBranchFinders = initializeBranchFinders(fcb, attributeAndClassificationProperties);
        return new ForestConfig<T, S, AttributeAndClassificationProperties<T>>(fcb.getTerminationConditions(), fcb.getScorer(), fcb.getNumTrees(), initializedBranchFinders,
                fcb.getLeafBuilder(), fcb.getBagging(), fcb.getDownSamplingTargetMinorityProportion(), fcb.getPruningStrategy(),
                fcb.getTreeFactory(), attributeAndClassificationProperties);
    }


    private List<BranchFinder<T>> initializeBranchFinders(ForestConfigBuilder<T, S, AttributeAndClassificationProperties<T>> fcb, AttributeAndClassificationProperties cp) {
        List<BranchFinder<T>> initializedBranchFinders = Lists.newArrayList();
        //TODO: TerminationConditions, attribute value
        for (BranchFinderBuilder<T, AttributeAndClassificationProperties<T>> branchFinderBuilder : fcb.getBranchFinderBuilders()) {
            initializedBranchFinders.add(branchFinderBuilder.buildBranchFinder(cp));
        }
        return initializedBranchFinders;

    }

    public ClassifierDataPropertiesTransformer<T, S> copy(){
        return new ClassifierDataPropertiesTransformer<>(considerBooleanAttributes);

    }
}
