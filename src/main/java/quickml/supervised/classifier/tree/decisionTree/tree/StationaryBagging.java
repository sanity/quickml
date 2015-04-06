package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.collect.Lists;
import quickml.collections.MapUtils;
import quickml.data.InstanceWithAttributesMap;

import java.util.HashSet;
import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class StationaryBagging<T extends InstanceWithAttributesMap> implements Bagging<T> {
    public List<T> baggedTrainingData;
    public List<T> outOfBagTrainingData;
    private static com.twitter.common.util.Random rand = com.twitter.common.util.Random.Util.fromSystemRandom(MapUtils.random);

    @Override
    public BaggedPair<T> separateTrainingDataFromOutOfBagData(List<T> trainingData) {
        this.baggedTrainingData = Lists.newArrayList();
        this.outOfBagTrainingData = Lists.newArrayList();

        HashSet<Integer> unusedDataIndices = new HashSet<>();
        for (int i = 0; i < trainingData.size(); i++) {
            unusedDataIndices.add(i);
        }
        for (int i = 0; i < trainingData.size(); i++) {
            int toAdd = rand.nextInt(trainingData.size());
            if (unusedDataIndices.contains(toAdd))
                unusedDataIndices.remove(toAdd);
            baggedTrainingData.add(trainingData.get(toAdd));
        }
        for (Integer index : unusedDataIndices) {
            outOfBagTrainingData.add(trainingData.get(index));
        }
        return new BaggedPair<>(baggedTrainingData, outOfBagTrainingData);

    }
}
