package quickml.supervised.classifier.splitOnAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributeClassifier extends AbstractClassifier {
    private static final long serialVersionUID = 2642074639257374588L;
    private final String attributeKey;
    private final Map<? extends Object, Integer> splitValToGroupId;
    private final Map<Integer, Classifier> splitModels;
    private final Integer defaultGroup;
    private static final Logger logger = LoggerFactory.getLogger(SplitOnAttributeClassifier.class);

    public SplitOnAttributeClassifier(String attributeKey, Map<? extends Object, Integer> splitValToGroupId, Integer defaultGroup, final Map<Integer, Classifier> splitModels) {
        logger.info("creating split classifier");
        this.attributeKey = attributeKey;
        this.splitModels = splitModels;
        this.splitValToGroupId = splitValToGroupId;
        this.defaultGroup = defaultGroup;
    }
    public Integer getDefaultGroup() {
        return defaultGroup;
    }

    public Map<? extends Object, Integer> getSplitValToGroupId() {
        return splitValToGroupId;
    }

    @Override
    public double getProbability(final AttributesMap attributes, final Object classification) {
        return getModelForAttributes(attributes).getProbability(attributes, classification);
    }

    @Override
    public double getProbabilityWithoutAttributes(final AttributesMap attributes, final Object classification, Set<String> attributesToIgnore) {
        return getModelForAttributes(attributes).getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
    }

    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        return getModelForAttributes(attributes).predict(attributes);
    }
    @Override
    public PredictionMap predictWithoutAttributes(final AttributesMap attributes, Set<String> attributesToIgnore) {
        return getModelForAttributes(attributes).predictWithoutAttributes(attributes, attributesToIgnore);
    }

    @Override
    public Object getClassificationByMaxProb(final AttributesMap attributes) {
        return getModelForAttributes(attributes).getClassificationByMaxProb(attributes);
    }

    public Map<Integer, Classifier> getSplitModels() {
        return splitModels;
    }

    private Classifier getModelForAttributes(AttributesMap attributes) {
        Object value = attributes.get(attributeKey);
        if (value == null) {
            throw new NullPointerException("not getting splitVar value");
        }
        Integer groupId = splitValToGroupId.get(value);
        if (groupId == null) {
            groupId = defaultGroup;
            logger.error("not getting a groupId");
        }
        return splitModels.get(groupId);
    }
}
