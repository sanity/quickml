package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.PredictionMapResults;

import java.util.*;

/**
 * Take a list of loss functions and keep a running total of the loss for each loss function per attribute
 */
public class AttributeLossTracker {

    private static final Logger logger = LoggerFactory.getLogger(AttributeLossTracker.class);
    private final LossFunctionTracker allAttributeLossTracker;

    private Map<String, LossFunctionTracker> attributeLossMap = Maps.newHashMap();

    public AttributeLossTracker(Set<String> attributes, List<ClassifierLossFunction> lossFunctions, ClassifierLossFunction primaryLossFunction) {
        for (String attribute : attributes) {
            attributeLossMap.put(attribute, new LossFunctionTracker(lossFunctions, primaryLossFunction));
        }
        allAttributeLossTracker = new LossFunctionTracker(lossFunctions, primaryLossFunction);
    }

    public void updateAttribute(String attribute, PredictionMapResults results) {
        attributeLossMap.get(attribute).updateLosses(results);
    }

    public void noMissingAttributeLoss(PredictionMapResults predictionMapResults) {
        allAttributeLossTracker.updateLosses(predictionMapResults);
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

    public List<String> getOrderedAttributes() {
        ArrayList<AttributeWithLoss> list = Lists.newArrayList();

        for (String attribute : attributeLossMap.keySet()) {
            list.add(new AttributeWithLoss(attribute, attributeLossMap.get(attribute).getPrimaryLoss()));
        }

        Collections.sort(list);

        List<String> attributes = Lists.newArrayList();
        for (AttributeWithLoss attributeWithLoss : list) {
            attributes.add(attributeWithLoss.attribute);
        }

        return attributes;
    }


    private class AttributeWithLoss implements Comparable<AttributeWithLoss> {
        private String attribute;
        private double loss;

        public AttributeWithLoss(String attribute, double loss) {
            this.attribute = attribute;
            this.loss = loss;
        }

        // Compare the other loss to this objects loss, we want the attributes with the
        // highest loss to come first (since removing them has the biggest affect on loss)
        @Override
        public int compareTo(AttributeWithLoss o) {
            return Double.compare(o.loss, loss);
        }
    }



}
