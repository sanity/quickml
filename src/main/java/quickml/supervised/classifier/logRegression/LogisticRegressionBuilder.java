package quickml.supervised.classifier.logRegression;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.tree.dataProcessing.AttributeCharacteristics;
import quickml.supervised.tree.dataProcessing.BasicTrainingDataSurveyor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.*;


/**
 * Created by alexanderhawk on 10/9/15.
 */
public class LogisticRegressionBuilder implements PredictiveModelBuilder<LogisticRegression, SparseClassifierInstance> {
    private  Map<Serializable, Double> classificationToClassNameMap;

    public final HashMap<String, Integer> nameToIndexMap;

    GradientDescent gradientDescent = new SGD();

    public LogisticRegressionBuilder(HashMap<String, Integer> nameToIndexMap) {

        this.nameToIndexMap = nameToIndexMap;
    }

    public LogisticRegressionBuilder gradientDescent(GradientDescent gradientDescent) {
        this.gradientDescent = gradientDescent;
        return this;
    }

    public LogisticRegressionBuilder classificationToClassNameMap(Map<Serializable, Double> classificationToClassNameMap){
        this.classificationToClassNameMap = classificationToClassNameMap;
        return this;
    }



    @Override
    public LogisticRegression buildPredictiveModel(final Iterable<SparseClassifierInstance> trainingData) {
        List<SparseClassifierInstance> trainingDataList = Utils.iterableToList(trainingData);
        double[] weights = gradientDescent.minimize(trainingDataList, nameToIndexMap.size());
        if (classificationToClassNameMap == null) {
            Set<Double> classifications = InstanceTransformerUtils.getClassifications(trainingDataList);
            return new LogisticRegression(weights,nameToIndexMap,classifications);
        } else {
            return new LogisticRegression(weights,nameToIndexMap,classificationToClassNameMap);
        }

    }

    @Override
    public void updateBuilderConfig(final Map<String, Serializable> config) {
        gradientDescent.updateBuilderConfig(config);
    }



}
