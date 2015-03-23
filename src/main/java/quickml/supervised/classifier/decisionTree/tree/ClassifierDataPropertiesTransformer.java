package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.collect.Maps;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;

import java.util.List;
import java.util.Map;
import static quickml.supervised.classifier.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public class ClassifierDataPropertiesTransformer<T extends InstanceWithAttributesMap> implements DataPropertiesTransformer<T> {

    public ForestConfig<T> createForestConfig(List<T> instances, ForestConfigBuilder configBuilder) {
        ClassificationProperties classificationProperties = ClassificationProperties.setDataProperties(instances);
        Map<ForestOptions, Object> updatedConfigSettings = getInitializedConfigSettings(configBuilder, classificationProperties);
        return new ForestConfig<>(updatedConfigSettings);


    }
//this and the method below can be static methods in InitializableUtils. since can be used for reg ression tress
    public Map<ForestOptions, Object> getInitializedConfigSettings(ForestConfigBuilder<T> fcb, ClassificationProperties cp) {
        Map<ForestOptions, Object> intializedConfigProperties = Maps.newHashMap();
    //this is where the Branch builders update. Each branch builder can have a different interface that used from a method like this.
        //in this case, ecah branch builders takes the cp's,  calls copy, and then updates the based on the cps.
        updateBranchBuilder(fcb, cp, intializedConfigProperties);
        intializedConfigProperties.put(LEAF_BUILDER, fcb.getLeafBuilder());
        intializedConfigProperties.put(SCORER, fcb.getScorer());
        intializedConfigProperties.put(MAX_DEPTH, fcb.getMaxDepth());
        intializedConfigProperties.put(MIN_LEAF_INSTANCES, fcb.getMinLeafInstances());
        intializedConfigProperties.put(MIN_SCORE, fcb.getMinScore());
        intializedConfigProperties.put(NUM_TREES, fcb.getNumTrees());

        return intializedConfigProperties;
    }

    private void updateBranchBuilder(ForestConfigBuilder<T> fcb, ClassificationProperties cp, Map<ForestOptions, Object> intializedConfigProperties) {
     //create inferface for Branchbuilder that implemetn initializable
        if (fcb.getCategoricalBranchBuilder().isPresent()) {
            CategoricalBranchBuilder<T> categoricalBranchBuilder = fcb.categoricalBranchBuilder.get();
            categoricalBranchBuilder.getInitializedBuilder(cp);
            intializedConfigProperties.put(CATEGORICAL_BRANCH_BUILDER, categoricalBranchBuilder);
        }
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
