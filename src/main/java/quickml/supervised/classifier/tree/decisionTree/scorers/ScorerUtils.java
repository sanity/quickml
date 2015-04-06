package quickml.supervised.classifier.tree.decisionTree.scorers;

import quickml.supervised.classifier.tree.decisionTree.tree.AttributeValueData;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;

import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class ScorerUtils {
    public static double getIntrinsicValueOfAttributeForClassifier(List<AttributeValueData> valuesWithCCs, double numTrainingExamples) {
        double informationValue = 0;
        double attributeValProb = 0;

        for (AttributeValueData attributeValueData : valuesWithCCs) {
            ClassificationCounter classificationCounter = attributeValueData.classificationCounter;
            attributeValProb = classificationCounter.getTotal() / (numTrainingExamples);//-insufficientDataInstances);
            informationValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
        }

        return informationValue;
    }

}
