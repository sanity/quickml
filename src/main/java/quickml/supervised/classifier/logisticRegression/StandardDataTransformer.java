package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.dataProcessing.AttributeCharacteristics;
import quickml.supervised.dataProcessing.BasicTrainingDataSurveyor;
import quickml.supervised.dataProcessing.instanceTranformer.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public abstract class StandardDataTransformer<D extends LinearRegressionDTO<D>> implements DataTransformer<ClassifierInstance, SparseClassifierInstance, D> {
    /**
     * class provides the method: transformInstances, to convert a set of classifier instances into instances that can be processed by
     * the LogisticRegressionBuilder.
     *
     * it assumes that all attributes with numeric values are numeric, and are not in need of one hot encoding.
     * product feature appendation as well as common co-occurences should be hyper-params within logistic regression.
     *
     */


    protected ProductFeatureAppender<ClassifierInstance> productFeatureAppender;

    protected boolean useProductFeatures = false;
    protected boolean doLabelToDigitConversion = true;
    protected int minObservationsOfAttribute;
    protected Map<Serializable, Double> numericClassLabels;

    public StandardDataTransformer() {}

    public StandardDataTransformer productFeatureAppender(ProductFeatureAppender<ClassifierInstance> productFeatureAppender) {
        this.productFeatureAppender = productFeatureAppender;
        return this;
    }

    public boolean usingProductFeatures(){
        return productFeatureAppender!=null;
    }

    public void doLabelToDigitConversion(boolean doLabelToDigitConversion){
        this.doLabelToDigitConversion = doLabelToDigitConversion;
    }


    public StandardDataTransformer minObservationsOfAttribute(int minObservationsOfAttribute) {
        this.minObservationsOfAttribute = minObservationsOfAttribute;
        return this;
    }


    public Map<Serializable, Double> getNumericClassLabels() {
        return numericClassLabels;
    }


    public StandardDataTransformer usingProductFeatures(boolean useProductFeatures) {
        this.useProductFeatures = useProductFeatures;
        return this;
    }


    //shouldn't be hard coded as a logistic Regression DTO..or at least it should be an abstract type...or a generic?
    @Override
    public abstract D transformData(List<ClassifierInstance> trainingData);



    static OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance>  getOneHotEncoder(List<ClassifierInstance> trainingData, int minObservationsOfAttribute) {
        BasicTrainingDataSurveyor<ClassifierInstance> btds = new BasicTrainingDataSurveyor<ClassifierInstance>(false);
        Map<String, AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);
        return new OneHotEncoder<>(attributeCharacteristics, new ClassifierInstanceFactory(), minObservationsOfAttribute);
    }


}
