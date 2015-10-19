package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.Utils;
import quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;
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

    private OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance> oneHotEncoder;
    private ProductFeatureAppender<Serializable, ClassifierInstance, ClassifierInstance> productFeatureAppender;
    private BinaryAndNumericAttributeNormalizer<Serializable, ClassifierInstance, ClassifierInstance> normalizer;
    private LabelToDigitConverter<Serializable, ClassifierInstance, ClassifierInstance> labelToDigitConverter;
    private ClassifierInstance2SparseClassifierInstance<Serializable, ClassifierInstance>  inputType2ReturnTypeTransformer;


    public List<SparseClassifierInstance> transformInstances(List<ClassifierInstance> trainingData, int minimumOccurencesOfAttribute, int minOverlap, boolean approximateOverlap, boolean useProductFeatures){
        List<InstanceTransformer<ClassifierInstance, ClassifierInstance>> input2InputTransformations = Lists.newArrayList();
        oneHotEncoder = getOneHotEncoder(trainingData);
        labelToDigitConverter = getLabelToDigitConverter(trainingData);
        input2InputTransformations.add(oneHotEncoder);
        input2InputTransformations.add(labelToDigitConverter);
        DataTransformer<ClassifierInstance, ClassifierInstance> dataTransformer = new DataTransformer<ClassifierInstance, ClassifierInstance>(
                input2InputTransformations, null);
        List<ClassifierInstance> oneHotEncoded = dataTransformer.transformInstances(trainingData);

        List<ClassifierInstance> instancesToNormalize;
        if (useProductFeatures) {
            productFeatureAppender = getProductFeatureAppender(oneHotEncoded, minimumOccurencesOfAttribute);
            input2InputTransformations = Lists.newArrayList();
            input2InputTransformations.add(productFeatureAppender);
            dataTransformer = new DataTransformer<ClassifierInstance, ClassifierInstance>(
                    input2InputTransformations, null);
            instancesToNormalize = InstanceTransformerUtils.addProductAttributes(oneHotEncoded, minimumOccurencesOfAttribute, minOverlap, approximateOverlap);
        } else {
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

    static ProductFeatureAppender<Serializable, ClassifierInstance, ClassifierInstance>  getProductFeatureAppender(List<ClassifierInstance> trainingData, int minimumCount) {
        return null;//new ProductFeatureAppender<>( productFeatureCounts, new ClassifierInstanceFactory(), minimumCount);
    }

    static OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance>  getOneHotEncoder(List<ClassifierInstance> trainingData) {
        BasicTrainingDataSurveyor<ClassifierInstance> btds = new BasicTrainingDataSurveyor<ClassifierInstance>(false);
        Map<String, AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);
        return new OneHotEncoder<>(attributeCharacteristics, new ClassifierInstanceFactory());
    }


}
