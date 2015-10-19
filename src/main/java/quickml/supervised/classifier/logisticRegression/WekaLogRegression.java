package quickml.supervised.classifier.logisticRegression;

/**
 * Created by alexanderhawk on 10/16/15.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;


import weka.classifiers.functions.Logistic;
import weka.core.Instances;

/**
 * Created by alexanderhawk on 10/9/15.
 */
public class WekaLogRegression implements Classifier {

    private final Logistic logistic;
    private final HashMap<String, Integer> nameToIndexMap;
    private final Instances instances;
    private static final Logger logger = LoggerFactory.getLogger(WekaLogRegression.class);

    public WekaLogRegression(final Logistic logistic, final HashMap<String, Integer> nameToIndexMap, Instances instances) {
        this.logistic = logistic;
        this.nameToIndexMap = nameToIndexMap;
        this.instances = instances;
    }

    @Override
    public double getProbability(final AttributesMap attributes, final Serializable classification) {
        try {
            return logistic.classifyInstance(WekaUtils.convertClassifierInstanceToSparseInstance(nameToIndexMap, new ClassifierInstance(attributes, classification), instances));
        } catch (Exception e) {
            logger.info("exception {}", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getProbabilityWithoutAttributes(final AttributesMap attributes, final Serializable classification, final Set<String> attributesToIgnore) {
        return 0;
    }

    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        return null;
    }

    @Override
    public PredictionMap predictWithoutAttributes(final AttributesMap attributes, final Set<String> attributesToIgnore) {
        return null;
    }

    @Override
    public Serializable getClassificationByMaxProb(final AttributesMap attributes) {
        return null;
    }
}
