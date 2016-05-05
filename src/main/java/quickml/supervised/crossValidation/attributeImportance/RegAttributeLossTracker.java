package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionLossFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Take a list of loss functions and keep a running total of the loss for each loss function per attribute
 */
public class RegAttributeLossTracker {

    private static final Logger logger = LoggerFactory.getLogger(RegAttributeLossTracker.class);
    private final RegLossFunctionTracker allAttributeLossTracker;

    private Map<String, RegLossFunctionTracker> attributeLossMap = Maps.newHashMap();

    public RegAttributeLossTracker(Set<String> attributes, List<RegressionLossFunction> lossFunctions, RegressionLossFunction primaryLossFunction) {
        for (String attribute : attributes) {
            attributeLossMap.put(attribute, new RegLossFunctionTracker(lossFunctions, primaryLossFunction));
        }
        allAttributeLossTracker = new RegLossFunctionTracker(lossFunctions, primaryLossFunction);
    }

    public void updateAttribute(String attribute, List<LabelPredictionWeight<Double, Double>> results) {
        attributeLossMap.get(attribute).updateLosses(results);
    }

    public void noMissingAttributeLoss(List<LabelPredictionWeight<Double, Double>> predictionMapResults) {
        allAttributeLossTracker.updateLosses(predictionMapResults);
    }

    public List<String> getOrderedAttributes() {
        List<String> attributes = Lists.newArrayList();
        for (AttributeWithLoss attributeWithLoss : getOrderedLosses()) {
            attributes.add(attributeWithLoss.getAttribute());
        }
        return attributes;
    }

    public List<AttributeWithLoss> getOrderedLosses() {
        List<AttributeWithLoss> list = Lists.newArrayList();
        for (String attribute : attributeLossMap.keySet()) {
            list.add(new AttributeWithLoss(attribute, attributeLossMap.get(attribute).getPrimaryLoss()));
        }
        Collections.sort(list);
        return list;
    }

    public double getOverallLoss() {
        return allAttributeLossTracker.getPrimaryLoss();
    }

    public void logResults() {
        logger.info("----- Attribute Loss Tracker  - Number of attributes {} ----", attributeLossMap.keySet().size());
        for (String attribute : getOrderedAttributes()) {
            logger.info("Attribute {}",  attribute);
            attributeLossMap.get(attribute).logLosses();
        }
    }






}
