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

    private Map<String, Losses> attributeLossMap = Maps.newHashMap();
    private final Map<String, ClassifierLossFunction> lossFunctions;
    private String primaryLossFunction;

    public AttributeLossTracker(Set<String> attributes, Map<String, ClassifierLossFunction> lossFunctions, String primaryLossFunction) {
        this.lossFunctions = lossFunctions;
        this.primaryLossFunction = primaryLossFunction;
        for (String attribute : attributes) {
            attributeLossMap.put(attribute, new Losses(lossFunctions));
        }
    }

    public void updateAttribute(String attribute, PredictionMapResults results) {
        if (!attributeLossMap.containsKey(attribute))
            attributeLossMap.put(attribute, new Losses(lossFunctions));
        attributeLossMap.get(attribute).updateLosses(results);
    }

    public void logResults() {
        logger.info("----- Attribute Loss Tracker  - Number of attributes {} ----", attributeLossMap.keySet().size());

        for (String attribute : getOrderedAttributes()) {
            Losses losses = attributeLossMap.get(attribute);
            for (String lossFunction : losses.functionLossMap.keySet()) {
                double loss = losses.functionLossMap.get(lossFunction).loss();
                logger.info("Attribute: {}, Lossfunction: {}, Loss: {}", attribute, lossFunction, loss);
            }
        }

    }

    public List<String> getOrderedAttributes() {
        ArrayList<AttributeWithLoss> list = Lists.newArrayList();

        for (String attribute : attributeLossMap.keySet()) {
            list.add(new AttributeWithLoss(attribute, attributeLossMap.get(attribute).functionLossMap.get(primaryLossFunction).loss()));
        }

        Collections.sort(list);

        List<String> attributes = Lists.newArrayList();
        for (AttributeWithLoss attributeWithLoss : list) {
            attributes.add(attributeWithLoss.attribute);
        }

        return attributes;
    }


    private class Losses {

        // Map of loss function name to the running loss for that function
        private Map<String, FunctionLoss> functionLossMap = Maps.newHashMap();

        public Losses(Map<String, ClassifierLossFunction> lossFunctions) {
            for (String lossFunctionName : lossFunctions.keySet()) {
                functionLossMap.put(lossFunctionName, new FunctionLoss(lossFunctions.get(lossFunctionName)));
            }
        }

        public void updateLosses(PredictionMapResults results) {
            for (FunctionLoss functionLoss : functionLossMap.values()) {
                functionLoss.updateLosses(results);
            }
        }

    }

    private class FunctionLoss implements Comparable<FunctionLoss> {

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        private ClassifierLossFunction lossFunction;

        public FunctionLoss(ClassifierLossFunction lossFunction) {
            this.lossFunction = lossFunction;
        }

        public void updateLosses(PredictionMapResults results) {
            runningLoss += lossFunction.getLoss(results) * results.totalWeight();
            runningWeightOfValidationSet += results.totalWeight();
        }

        public double loss() {
            return runningWeightOfValidationSet > 0 ? runningLoss / runningWeightOfValidationSet : 0;
        }

        @Override
        public int compareTo(FunctionLoss o) {
            return Double.compare(loss(), o.loss());
        }
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
