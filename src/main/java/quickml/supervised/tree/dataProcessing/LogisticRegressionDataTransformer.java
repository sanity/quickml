package quickml.supervised.tree.dataProcessing;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.Utils;
import quickml.supervised.classifier.logRegression.InstanceTransformerUtils;
import quickml.supervised.classifier.logRegression.SparseClassifierInstance;
import quickml.supervised.tree.dataProcessing.instanceTranformer.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.determineNumericClassLabels;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class LogisticRegressionDataTransformer {

    private OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance> oneHotEncoder;
    private ProductFeatureAppender<Serializable, ClassifierInstance, ClassifierInstance> productFeatureAppender;
    private BinaryAndNumericAttributeNormalizer<Serializable, ClassifierInstance, ClassifierInstance> normalizer;
    private LabelToDigitConverter<Serializable, ClassifierInstance, ClassifierInstance> labelToDigitConverter;
    private InstanceTransformer<ClassifierInstance, SparseClassifierInstance> inputType2ReturnTypeTransformer;

    public static List<SparseClassifierInstance> transformInstaces(List<ClassifierInstance> trainingData, int minimumCount){

        OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance> oneHotEncoder = getOneHotEncoder(trainingData);
        ProductFeatureAppender<Serializable, ClassifierInstance, ClassifierInstance> productFeatureAppender = getProductFeatureAppender(trainingData, minimumCount);
        BinaryAndNumericAttributeNormalizer<Serializable, ClassifierInstance, ClassifierInstance> normalizer = getNormalizer(trainingData);
        LabelToDigitConverter<Serializable, ClassifierInstance, ClassifierInstance> labelToDigitConverter = getLabelToDigitConverter(trainingData);

        List<InstanceTransformer<ClassifierInstance, ClassifierInstance>> preSparsificationTransformations = Lists.newArrayList();
        preSparsificationTransformations.add(oneHotEncoder);
        preSparsificationTransformations.add(productFeatureAppender);
        preSparsificationTransformations.add(normalizer);
        preSparsificationTransformations.add(labelToDigitConverter);

        InstanceTransformer<ClassifierInstance, SparseClassifierInstance> classifierInstance2SparseClassifierInstanceTransformer = getInputType2ReturnTypeTransformer(trainingData);
        DataTransformer<ClassifierInstance, SparseClassifierInstance> dataTransformer = new DataTransformer<ClassifierInstance, SparseClassifierInstance>(
                preSparsificationTransformations, classifierInstance2SparseClassifierInstanceTransformer);

        return dataTransformer.transformInstances(trainingData);
    }

    static  InstanceTransformer<ClassifierInstance, SparseClassifierInstance> getInputType2ReturnTypeTransformer(List<ClassifierInstance> trainingData) {
        return new ClassifierInstance2SparseClassifierInstance<>(trainingData);
    }

    static LabelToDigitConverter<Serializable, ClassifierInstance, ClassifierInstance> getLabelToDigitConverter(List<ClassifierInstance> trainingData) {
        return new LabelToDigitConverter<>(new ClassifierInstanceFactory(), trainingData);
    }

    static BinaryAndNumericAttributeNormalizer<Serializable, ClassifierInstance, ClassifierInstance> getNormalizer(List<ClassifierInstance> trainingData) {
        return new BinaryAndNumericAttributeNormalizer<>(trainingData, new ClassifierInstanceFactory());
    }

    static ProductFeatureAppender<Serializable, ClassifierInstance, ClassifierInstance>  getProductFeatureAppender(List<ClassifierInstance> trainingData, int minimumCount) {
        Map<String, Integer> productFeatureCounts = InstanceTransformerUtils.getAttributeProductCounts(trainingData);
        return new ProductFeatureAppender<>(
                productFeatureCounts, new ClassifierInstanceFactory(), minimumCount);
    }

    static OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance>  getOneHotEncoder(List<ClassifierInstance> trainingData) {
        BasicTrainingDataSurveyor<ClassifierInstance> btds = new BasicTrainingDataSurveyor<ClassifierInstance>(false);
        Map<String, AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);
        return new OneHotEncoder<>(attributeCharacteristics, new ClassifierInstanceFactory());
    }


}
