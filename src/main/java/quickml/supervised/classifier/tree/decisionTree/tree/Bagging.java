package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;

import java.util.HashSet;
import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface Bagging<T extends InstanceWithAttributesMap> {
    BaggedPair<T> separateTrainingDataFromOutOfBagData(List<T> trainingData);

    class BaggedPair<T extends InstanceWithAttributesMap> {
        public List<T> baggedTrainingData;
        public List<T> outOfBagTrainingData;

        public BaggedPair(List<T> baggedTrainingData, List<T> outOfBagTrainingData) {
            this.baggedTrainingData = baggedTrainingData;
            this.outOfBagTrainingData = outOfBagTrainingData;
        }
    }
}
