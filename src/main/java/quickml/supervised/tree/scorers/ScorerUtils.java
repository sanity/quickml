package quickml.supervised.tree.scorers;

import quickml.supervised.tree.decisionTree.tree.AttributeValueData;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class ScorerUtils {
    public static double getIntrinsicValueOfAttributeForClassifier(List<AttributeValueData> valuesWithCCs, double numTrainingExamples) {
        double informationValue = 0;
        double attributeValProb = 0;

        for (AttributeValueData attributeValueData : valuesWithCCs) {
            ClassificationCounter classificationCounter = attributeValueData.termStatistics;
            attributeValProb = classificationCounter.getTotal() / (numTrainingExamples);//-insufficientDataInstances);
            informationValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
        }

        return informationValue;
    }

}
