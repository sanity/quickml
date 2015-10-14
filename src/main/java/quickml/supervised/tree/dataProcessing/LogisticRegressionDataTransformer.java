package quickml.supervised.tree.dataProcessing;

import com.google.common.collect.Lists;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.Utils;
import quickml.supervised.classifier.logRegression.SparseClassifierInstance;
import quickml.supervised.tree.dataProcessing.instanceTranformer.InstanceTransformer;
import quickml.supervised.tree.dataProcessing.instanceTranformer.OneHotEncoder;
import quickml.supervised.tree.dataProcessing.instanceTranformer.ProductFeatureAppender;

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

    public List<SparseClassifierInstance> transformInstaces(List<ClassifierInstance> trainingData){
        BasicTrainingDataSurveyor<ClassifierInstance> btds = new BasicTrainingDataSurveyor<ClassifierInstance>(false);
        Map<String, AttributeCharacteristics> attributeCharacteristics = btds.getMapOfAttributesToAttributeCharacteristics(trainingData);

        List<InstanceTransformer<ClassifierInstance, ClassifierInstance>> preSparsificationTransformations = Lists.newArrayList();
        oneHotEncoder = new OneHotEncoder<>(attributeCharacteristics, new ClassifierInstanceFactory());
        preSparsificationTransformations.add(oneHotEncoder);
     //   preSparsificationTransformations.add(new ProductFeatureAppender<Serializable, ClassifierInstance, ClassifierInstance>())
  return null;
    }



}
