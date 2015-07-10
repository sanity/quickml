package quickml.supervised.tree.decisionTree.reducers;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.branchFinders.SplittingUtilsTest;
import quickml.supervised.tree.decisionTree.nodes.DTCatBranch;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 6/29/15.
 */
public class DTCatOldBranchReducerTest {

    @Test
    public void getAttributeStatsTest() {
        List<ClassifierInstance> instances = getInstances();
        DTCatBranchReducer<ClassifierInstance> reducer = new DTCatBranchReducer<>(instances);
        Optional<AttributeStats<ClassificationCounter>> attributeStatsOptional = reducer.getAttributeStats("t");
        AttributeStats<ClassificationCounter> attributeStats = attributeStatsOptional.get();
        Assert.assertEquals(attributeStats.getStatsOnEachValue().size(), 2);
        ClassificationCounter first = attributeStats.getStatsOnEachValue().get(0);
        assertionsAboutInstances(first);
        ClassificationCounter second = attributeStats.getStatsOnEachValue().get(0);
        assertionsAboutInstances(second);

    }


    private void assertionsAboutInstances(ClassificationCounter first) {
        if(first.attrVal.equals("1.0")) {
            Assert.assertEquals(first.getCount(1.0), 2.0, 1E-5);
            Assert.assertEquals(first.getCount(0.0), 2.0, 1E-5);
        } else {
            Assert.assertEquals(first.getCount(1.0), 3.0, 1E-5);
            Assert.assertEquals(first.getCount(0.0), 1.0, 1E-5);
        }
    }

    public static List<ClassifierInstance> getInstances() {
        List<ClassifierInstance> td = Lists.newArrayList();

        AttributesMap atMap = AttributesMap.newHashMap();
        atMap.put("t", "1.0");
        td.add(new ClassifierInstance(atMap, 0.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "1.0");
        td.add(new ClassifierInstance(atMap, 0.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "1.0");
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "1.0");
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "2.0");
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "2.0");
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "2.0");
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", "2.0");
        td.add(new ClassifierInstance(atMap, 0.0));
        return td;
    }

}