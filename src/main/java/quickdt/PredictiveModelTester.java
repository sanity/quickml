package quickdt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 7/17/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
/*
 * PredictiveModelTester provides some utility functions for testing model accuracy and
 * variable importance. Note that it is important the testing data passed in is distinct
 * from the data used for training.
 */

public class PredictiveModelTester {
    public final PredictiveModel model;
    public final Serializable classification;

    public PredictiveModelTester(PredictiveModel model, Serializable classification) {
        this.model = model;
        this.classification = classification;
    }

    public double getRMSE(final Iterable<? extends AbstractInstance> testingData) {
        ErrorTabulator tabulator = new ErrorTabulator();

        for(AbstractInstance instance : testingData) {
            tabulator.tabulateInstanceError(instance.getAttributes(), instance.getClassification());
        }
        return tabulator.getRMSE();
    }

    public LinkedHashMap<String, Double> getVariableImportance(final Iterable<? extends AbstractInstance> testingData ) {
        Map<String, Double> scores = Maps.newHashMap();
        double baseError = getRMSE(testingData);
        final AbstractInstance sampleInstance = Iterables.get(testingData, 0);
        Set<String> allAttributes = sampleInstance.getAttributes().keySet();
        List<? extends AbstractInstance> data = Lists.newArrayList(testingData);
        for(String attribute : allAttributes) {
            ErrorTabulator tabulator = new ErrorTabulator();

            for(AbstractInstance instance : testingData) {
                Attributes attributes = instance.getAttributes();
                HashMapAttributes fuzzed = new HashMapAttributes();
                for(String a : allAttributes) {
                    if( a.equals(attribute)) {
                        fuzzed.put(attribute, data.get(Misc.random.nextInt(data.size())).getAttributes().get(attribute));
                    }
                    else {
                        fuzzed.put(a, attributes.get(a));
                    }
                }
                tabulator.tabulateInstanceError(fuzzed, instance.getClassification());

            }
            scores.put(attribute, tabulator.getRMSE()-baseError);
        }

        // Sort the variables by importance.
        TreeSet<Map.Entry<String,Double>> orderedEntries = Sets.newTreeSet(new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                } else if (o1.getValue() > o2.getValue()) {
                    return -1;
                } else {
                    return o1.getKey().compareTo(o2.getKey());
                }
            }
        });
        orderedEntries.addAll(scores.entrySet());
        LinkedHashMap<String,Double> returnVal = Maps.newLinkedHashMap();
        for(Map.Entry<String, Double> e : orderedEntries) {
            returnVal.put(e.getKey(), e.getValue());
        }
        return returnVal;
    }

    private class ErrorTabulator {
        public double sumSquaredError = 0.0;
        public int instanceCount = 0;

        public void tabulateInstanceError(Attributes attributes, Serializable instanceClassification) {
            double score = model.getProbability(attributes, classification);
            if (instanceClassification == classification) {
                sumSquaredError += Math.pow(1.0-score, 2);
                instanceCount++;

            } else {
                sumSquaredError += Math.pow(score, 2);
                instanceCount++;
            }
        }

        public double getRMSE() {
            return Math.sqrt(sumSquaredError/instanceCount);
        }
    }
}
