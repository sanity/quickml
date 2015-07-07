package quickml.supervised.tree.decisionTree.reducers;

import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.List;

/**
 * Created by alexanderhawk on 6/29/15.
 */
public class BinaryCatOldBranchReducerTest {

    @Test
    public void binCatReducerGetAttributeStatsTest() {
        List<ClassifierInstance> instances = DTCatOldBranchReducerTest.<ClassifierInstance>getInstances();
        BinaryCatBranchReducer<ClassifierInstance> reducer = new BinaryCatBranchReducer<>(0.0);
        reducer.setTrainingData(instances);
        Optional<AttributeStats<ClassificationCounter>> attributeStatsOptional = reducer.getAttributeStats("t");
        AttributeStats<ClassificationCounter> attributeStats = attributeStatsOptional.get();
        Assert.assertEquals(attributeStats.getStatsOnEachValue().size(), 2);
        ClassificationCounter first = attributeStats.getStatsOnEachValue().get(0);

        //test correct ordering that first comes before second
        Assert.assertEquals(first.getCount(1.0), 2.0, 1E-5);
        Assert.assertEquals(first.getCount(0.0), 2.0, 1E-5);
        ClassificationCounter second = attributeStats.getStatsOnEachValue().get(1);
        Assert.assertEquals(second.getCount(1.0), 3.0, 1E-5);
        Assert.assertEquals(second.getCount(0.0), 1.0, 1E-5);

    }

}