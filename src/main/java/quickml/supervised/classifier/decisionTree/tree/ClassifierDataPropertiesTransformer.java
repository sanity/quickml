package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.decisionTree.Scorer;

import java.util.List;
import java.util.Map;
import static quickml.supervised.classifier.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public class ClassifierDataPropertiesTransformer<T extends InstanceWithAttributesMap> implements DataPropertiesTransformer<T> {

    public ForestConfig<T> createForestConfig(List<T> instances, ForestConfigBuilder configBuilder) {
        ClassificationProperties classificationProperties = ClassificationProperties.setDataProperties(instances);
        //get Numeric and categorical attributes here
        Map<ForestOptions, Object> cfg = getInitializedConfigSettings(configBuilder, classificationProperties, instances);
        return new ForestConfig<T>((Scorer)cfg.get(SCORER), (Double)cfg.get(MIN_SCORE), (Integer)cfg.get(MIN_LEAF_INSTANCES), (Integer)cfg.get(NUM_TREES), (Integer)cfg.get(MAX_DEPTH), (Iterable<BranchFinderBuilder<T>>)cfg.get(BRANCH_BUILDERS), (LeafBuilder<T>)cfg.get(LEAF_BUILDER));


    }
    public Map<ForestOptions, Object> getInitializedConfigSettings(ForestConfigBuilder<T> fcb, ClassificationProperties cp, List<T> instances) {
        Map<ForestOptions, Object> intializedConfigProperties = Maps.newHashMap();
        List<BranchFinder<T>> branchFinders = initializeBranchFinders(fcb, cp, instances);
        intializedConfigProperties.put(LEAF_BUILDER, fcb.getLeafBuilder());
        intializedConfigProperties.put(SCORER, fcb.getScorer());
        intializedConfigProperties.put(MAX_DEPTH, fcb.getMaxDepth());
        intializedConfigProperties.put(MIN_LEAF_INSTANCES, fcb.getMinLeafInstances());
        intializedConfigProperties.put(MIN_SCORE, fcb.getMinScore());
        intializedConfigProperties.put(NUM_TREES, fcb.getNumTrees());

        return intializedConfigProperties;
    }

    private List<BranchFinder<T>>  initializeBranchFinders(ForestConfigBuilder<T> fcb, ClassificationProperties cp, List<T> instances) {
        List<BranchFinder<T>> initializedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<T> BranchFinderBuilder : fcb.getBranchFinderBuilders()) {
            initializedBranchFinderBuilders.add(BranchFinderBuilder.buildBranchFinder(cp));
        }
        intializedConfigProperties.put(BRANCH_BUILDERS, initializedBranchFinderBuilders);
    }

    public ClassifierDataPropertiesTransformer<T> copy(){
        return new ClassifierDataPropertiesTransformer<>();

    }
// // put in Branch Builder
  /*  public ForestConfig(List<? extends InstanceWithAttributesMap> instances, ForestConfigBuilder forestConfigBuilder) {
        copyForestConfig(forestConfigBuilder);
        if (buildClassificationTrees && attributeValueIgnoringStrategy == null) {
            classificationProperties = ClassificationProperties.setDataProperties(instances);
        }
        setAttributeValueIgnoringStrategy();
    }


    private void setAttributeValueIgnoringStrategy() {
        AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
        if (classificationProperties.classificationsAreBinary()) {
            attributeValueIgnoringStrategy =
                    new BinaryClassAttributeValueIgnoringStrategy(
                            (BinaryClassificationProperties)classificationProperties,
                            attributeValueObservationsThreshold);
        } else {
            attributeValueIgnoringStrategy = new MultiClassAtributeValueIgnoringStrategy(attributeValueObservationsThreshold);

        }
        this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;

    }
    */
}
