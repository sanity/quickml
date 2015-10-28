package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.Utils;
import quickml.supervised.dataProcessing.AttributeCharacteristics;
import quickml.supervised.dataProcessing.BasicTrainingDataSurveyor;
import quickml.supervised.dataProcessing.DataTransformer;
import quickml.supervised.dataProcessing.instanceTranformer.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class LogisticRegressionDataTransformer {
    /**
     * class provides the method: transformInstances, to convert a set of classifier instances into instances that can be processed by
     * the LogisticRegressionBuilder.
     *
     * it assumes that all attributes with numeric values are numeric, and are not in need of one hot encoding.
     * product feature appendation as well as common co-occurences should be hyper-params within logistic regression.
     *
     */
    /*Options, wrap logistic regression? in a new logistic regression class that has a logistic reg transformer?
            * Or change sparse classifier instance as the the type of Logistic Regression?  I almost prefer this.  So now to use it...one just passes in a normal list of training instances
    */

    private OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance> oneHotEncoder;
    private ProductFeatureAppender<ClassifierInstance> productFeatureAppender;
    private BinaryAndNumericAttributeNormalizer<Serializable, ClassifierInstance, ClassifierInstance> normalizer;
    private LabelToDigitConverter<Serializable, ClassifierInstance, ClassifierInstance> labelToDigitConverter;
    private ClassifierInstance2SparseClassifierInstance<Serializable, ClassifierInstance>  inputType2ReturnTypeTransformer;
    private boolean useProductFeatures = false;
    private int minObservationsOfAttribute;

    public LogisticRegressionDataTransformer productFeatureAppender(CommonCoocurrenceProductFeatureAppender<ClassifierInstance> productFeatureAppender) {
        this.productFeatureAppender = productFeatureAppender;
        return this;
    }

    public LogisticRegressionDataTransformer minObservationsOfAttribute(int minObservationsOfAttribute) {
        this.minObservationsOfAttribute = minObservationsOfAttribute;
        return this;
    }

    public LogisticRegressionDataTransformer() {
/**one needs to set useProductFeatures if this appender is to be put to use*/
        productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<>().setMinObservationsOfRawAttribute(10).setAllowCategoricalProductFeatures(true)
        .setAllowNumericProductFeatures(true)
        .setApproximateOverlap(true)
        .setMinOverlap(20);
    }

    public LogisticRegressionDataTransformer(CommonCoocurrenceProductFeatureAppender<ClassifierInstance> productFeatureAppender) {
        this.productFeatureAppender = productFeatureAppender;
    }

    public LogisticRegressionDataTransformer(boolean useProductFeatures) {
        this.useProductFeatures = useProductFeatures;
    }

    public LogisticRegressionDataTransformer useProductFeatures(boolean useProductFeatures) {
        this.useProductFeatures = useProductFeatures;
        return this;
    }

    public List<SparseClassifierInstance> transformInstances(List<ClassifierInstance> trainingData){
        List<InstanceTransformer<ClassifierInstance, ClassifierInstance>> input2InputTransformations = Lists.newArrayList();
        labelToDigitConverter = getLabelToDigitConverter(trainingData);
        input2InputTransformations.add(labelToDigitConverter);
        DataTransformer<ClassifierInstance, ClassifierInstance> dataTransformer = new DataTransformer<ClassifierInstance, ClassifierInstance>(
                input2InputTransformations, null);
        List<ClassifierInstance> relabeled = dataTransformer.transformInstances(trainingData);

        oneHotEncoder = getOneHotEncoder(relabeled, minObservationsOfAttribute);
        List<ClassifierInstance> oneHotEncoded = oneHotEncoder.transformAll(relabeled);



        List<ClassifierInstance> instancesToNormalize;
        if (useProductFeatures) {
            instancesToNormalize = productFeatureAppender.addProductAttributes(oneHotEncoded);
        }  else {
            instancesToNormalize = oneHotEncoded;
        }

        normalizer = getNormalizer(instancesToNormalize);
        input2InputTransformations = Lists.newArrayList();
        input2InputTransformations.add(normalizer);
        dataTransformer = new DataTransformer<ClassifierInstance, ClassifierInstance>(
                input2InputTransformations, null);
        List<ClassifierInstance> normalized = dataTransformer.transformInstances(instancesToNormalize);


        inputType2ReturnTypeTransformer = getInputType2ReturnTypeTransformer(normalized);
        input2InputTransformations = Lists.newArrayList();
        DataTransformer<ClassifierInstance, SparseClassifierInstance> inputType2OutputdataTransformer = new DataTransformer<ClassifierInstance, SparseClassifierInstance>(
                input2InputTransformations, inputType2ReturnTypeTransformer);

        return inputType2OutputdataTransformer.transformInstances(normalized);
    }

    public HashMap<String, Integer> getNameToIndexMap(){
        return inputType2ReturnTypeTransformer.getNameToIndexMap();
    }

    public Map<String, Utils.MeanStdMaxMin> getMeanStdMaxMins(){
        return normalizer.getMeanStdMaxMins();
    }

    static  ClassifierInstance2SparseClassifierInstance<Serializable, ClassifierInstance> getInputType2ReturnTypeTransformer(List<ClassifierInstance> trainingData) {
        return new ClassifierInstance2SparseClassifierInstance<>(trainingData);
    }

    static LabelToDigitConverter<Serializable, ClassifierInstance, ClassifierInstance> getLabelToDigitConverter(List<ClassifierInstance> trainingData) {
        return new LabelToDigitConverter<>(new ClassifierInstanceFactory(), trainingData);
    }

    static BinaryAndNumericAttributeNormalizer<Serializable, ClassifierInstance, ClassifierInstance> getNormalizer(List<ClassifierInstance> trainingData) {
        return new BinaryAndNumericAttributeNormalizer<>(trainingData, new ClassifierInstanceFactory(), new BinaryAndNumericAttributeNormalizer.NoNormalizationCondition() {
            @Override
            public boolean noNormalization(String key) {
                return false;//key.contains("timeOfArrival-");
            }
        });
    }

    static OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance>  getOneHotEncoder(List<ClassifierInstance> trainingData, int minObservationsOfAttribute) {
        BasicTrainingDataSurveyor<ClassifierInstance> btds = new BasicTrainingDataSurveyor<ClassifierInstance>(false);
        Map<String, AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);
        return new OneHotEncoder<>(attributeCharacteristics, new ClassifierInstanceFactory(), minObservationsOfAttribute);
    }


}
