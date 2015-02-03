package quickml.supervised.alternative.attributeImportanceFinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossFunction;
import quickml.supervised.alternative.crossValidationLoss.PredictionMapResults;

import java.util.*;

public class AttributeLossTracker {

    private static final Logger logger = LoggerFactory.getLogger(AttributeLossTracker.class);

    private Map<String, LossFunctionTracker> attributeLossMap = Maps.newHashMap();
    private final List<ClassifierLossFunction> lossFunctions;
    private String primaryLossFunction;

    public AttributeLossTracker(Set<String> attributes, List<ClassifierLossFunction> lossFunctions, String primaryLossFunction) {
        this.lossFunctions = lossFunctions;
        this.primaryLossFunction = primaryLossFunction;
        for (String attribute : attributes) {
            attributeLossMap.put(attribute, new LossFunctionTracker(lossFunctions));
        }
    }

    public void updateAttribute(String attribute, PredictionMapResults results) {
        if (!attributeLossMap.containsKey(attribute))
            attributeLossMap.put(attribute, new LossFunctionTracker(lossFunctions));
        attributeLossMap.get(attribute).updateLosses(results);
    }

    public void logResults() {
        logger.info("----- Attribute Loss Tracker  - Number of attributes {} ----", attributeLossMap.keySet().size());

        for (String attribute : getOrderedAttributes()) {
            LossFunctionTracker lossFunctionTracker = attributeLossMap.get(attribute);

            for (String lossFunction : lossFunctionTracker.lossFunctionNames()) {
                double loss = lossFunctionTracker.getLossForFunction(lossFunction);
                logger.info("Attribute: {}, Lossfunction: {}, Loss: {}", attribute, lossFunction, loss);
            }
        }
    }

    public List<String> getOrderedAttributes() {
        ArrayList<AttributeWithLoss> list = Lists.newArrayList();

        for (String attribute : attributeLossMap.keySet()) {
            list.add(new AttributeWithLoss(attribute, attributeLossMap.get(attribute).getLossForFunction(primaryLossFunction)));
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
