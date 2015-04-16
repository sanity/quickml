package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public interface Bagging {
    <L, T extends InstanceWithAttributesMap<L>> TrainingDataPair<L, T> separateTrainingDataFromOutOfBagData(List<T> trainingData);

    class TrainingDataPair<L, T extends InstanceWithAttributesMap<L>> {
        public List<T> trainingData;
        public List<T> outOfBagTrainingData;

        public TrainingDataPair(List<T> trainingData, List<T> outOfBagTrainingData) {
            this.trainingData = trainingData;
            this.outOfBagTrainingData = outOfBagTrainingData;
        }
    }
}
