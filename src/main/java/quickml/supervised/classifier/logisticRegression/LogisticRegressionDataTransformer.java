package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.Utils;
import quickml.supervised.dataProcessing.AttributeCharacteristics;
import quickml.supervised.dataProcessing.BasicTrainingDataSurveyor;
import quickml.supervised.dataProcessing.ElementaryDataTransformer;
import quickml.supervised.dataProcessing.instanceTranformer.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class LogisticRegressionDataTransformer implements DataTransformer<ClassifierInstance, LogisticRegressionDTO> {
    //to do: get label to digit Map and stick in DTO (and transform to logistic regression eventually)
    //make LogisticRegressionBuilder use this class and not be tightly coupled to mean normalization (e.g. allow log^2 values)
    //make cross validator take a datetransformer (specifically, the Logistic regression PMB, and then do the data normalization
    // and set the date time extractor)


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
    private boolean doLabelToDigitConversion = true;
    private int minObservationsOfAttribute;
    private Map<Serializable, Double> numericClassLabels;

    public LogisticRegressionDataTransformer() {}

    public LogisticRegressionDataTransformer productFeatureAppender(ProductFeatureAppender<ClassifierInstance> productFeatureAppender) {
        this.productFeatureAppender = productFeatureAppender;
        return this;
    }

    public boolean usingProductFeatures(){
        return productFeatureAppender!=null;
    }

    public void doLabelToDigitConversion(boolean doLabelToDigitConversion){
        this.doLabelToDigitConversion = doLabelToDigitConversion;
    }


    public LogisticRegressionDataTransformer minObservationsOfAttribute(int minObservationsOfAttribute) {
        this.minObservationsOfAttribute = minObservationsOfAttribute;
        return this;
    }


    public Map<Serializable, Double> getNumericClassLabels() {
        return numericClassLabels;
    }


    public LogisticRegressionDataTransformer usingProductFeatures(boolean useProductFeatures) {
        this.useProductFeatures = useProductFeatures;
        return this;
    }

    //shouldn't be hard coded as a logistic Regression DTO..or at least it should be an abstract type...or a generic?
    public LogisticRegressionDTO transformData(List<ClassifierInstance> trainingData){
        List<InstanceTransformer<ClassifierInstance, ClassifierInstance>> input2InputTransformations = Lists.newArrayList();
        List<ClassifierInstance> firstStageData;
        if (doLabelToDigitConversion) {
            labelToDigitConverter = getLabelToDigitConverter(trainingData);
            input2InputTransformations.add(labelToDigitConverter);
            ElementaryDataTransformer<ClassifierInstance, ClassifierInstance> dataTransformer = new ElementaryDataTransformer<ClassifierInstance, ClassifierInstance>(
                    input2InputTransformations, null);
            firstStageData = dataTransformer.transformInstances(trainingData);
            numericClassLabels = labelToDigitConverter.getNumericClassLabels();
        }
        else {
            firstStageData = trainingData;
        }

        oneHotEncoder = getOneHotEncoder(firstStageData, minObservationsOfAttribute);
        List<ClassifierInstance> oneHotEncoded = oneHotEncoder.transformAll(firstStageData);


        List<ClassifierInstance> instancesToNormalize;
        if (useProductFeatures) {
            instancesToNormalize = productFeatureAppender.addProductAttributes(oneHotEncoded);
        }  else {
            instancesToNormalize = oneHotEncoded;
        }

        normalizer = getNormalizer(instancesToNormalize);
        input2InputTransformations = Lists.newArrayList();
        input2InputTransformations.add(normalizer);
        ElementaryDataTransformer dataTransformer = new ElementaryDataTransformer(
                input2InputTransformations, null);
        List<ClassifierInstance> normalized = dataTransformer.transformInstances(instancesToNormalize);


        inputType2ReturnTypeTransformer = getInputType2ReturnTypeTransformer(normalized);
        input2InputTransformations = Lists.newArrayList();
        ElementaryDataTransformer<ClassifierInstance, SparseClassifierInstance> inputType2OutputdataTransformer = new ElementaryDataTransformer<ClassifierInstance, SparseClassifierInstance>(
                input2InputTransformations, inputType2ReturnTypeTransformer);

        List<SparseClassifierInstance> sparseClassifierInstances = inputType2OutputdataTransformer.transformInstances(normalized);
        return new LogisticRegressionDTO(sparseClassifierInstances, getNameToIndexMap(), getMeanStdMaxMins(), numericClassLabels);
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
