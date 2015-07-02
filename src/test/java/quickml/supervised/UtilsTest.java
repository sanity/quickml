package quickml.supervised;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.branchFinders.SplittingUtilsTest;
import quickml.supervised.tree.decisionTree.nodes.DTCatBranch;
import quickml.supervised.tree.decisionTree.nodes.DTNumBranch;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 6/29/15.
 */
public class UtilsTest {

    @Test
    public void setTrueAndFalseTrainingSetsTestForCatBranch(){
        Set<Serializable> trueSet = Sets.newHashSet();

        //check trivial case first where i give it a training list already sorted correctly
        trueSet.add("1.0");
        DTCatBranch branch = new DTCatBranch(null, "t",trueSet, .5, .5, new ClassificationCounter());
        Utils.TrueFalsePair<ClassifierInstance> tfPair = Utils.<ClassifierInstance>setTrueAndFalseTrainingSets(getInstances(), branch);
        Assert.assertEquals(tfPair.falseTrainingSet.size(), 4);
        Assert.assertEquals(tfPair.trueTrainingSet.size(), 4);
        for(ClassifierInstance instance : tfPair.falseTrainingSet) {
            Assert.assertEquals(instance.getAttributes().get("t"), "2.0");
        }
        for(ClassifierInstance instance : tfPair.trueTrainingSet) {
            Assert.assertEquals(instance.getAttributes().get("t"), "1.0");
        }
        //check non trivial case where data is in reverse order of what it needs to be
        trueSet = Sets.newHashSet();
        trueSet.add("2.0");
        branch = new DTCatBranch(null, "t", trueSet, .5, .5, new ClassificationCounter());
        tfPair = Utils.<ClassifierInstance>setTrueAndFalseTrainingSets(getInstances(),branch);
        Assert.assertEquals(tfPair.falseTrainingSet.size(), 4);
        Assert.assertEquals(tfPair.trueTrainingSet.size(), 4);
        for(ClassifierInstance instance : tfPair.falseTrainingSet) {
            Assert.assertEquals(instance.getAttributes().get("t"), "1.0");
        }
        for(ClassifierInstance instance : tfPair.trueTrainingSet) {
            Assert.assertEquals(instance.getAttributes().get("t"), "2.0");
        }
    }

    @Test
    public void setTrueAndFalseTrainingSetsTestForNumBranch(){

        //check trivial case first where i give it a training list already sorted correctly
        DTNumBranch branch = new DTNumBranch(null, "t", 0.5, .5, new ClassificationCounter(), 4.5);
        Utils.TrueFalsePair<ClassifierInstance> tfPair = Utils.<ClassifierInstance>setTrueAndFalseTrainingSets(SplittingUtilsTest.getExtendedInstances(),branch);
        Assert.assertEquals(tfPair.falseTrainingSet.size(), 4);
        Assert.assertEquals(tfPair.trueTrainingSet.size(), 4);
        for(ClassifierInstance instance : tfPair.falseTrainingSet) {
            Assert.assertTrue((Double)(instance.getAttributes().get("t")) < 4.5);
        }
        for(ClassifierInstance instance : tfPair.trueTrainingSet) {
            Assert.assertTrue((Double) (instance.getAttributes().get("t")) > 4.5);
        }
        //check non trivial case where data is in reverse order of what it needs to be
         branch = new DTNumBranch(null, "t", 0.5, .5, new ClassificationCounter(), 6.5);

        tfPair = Utils.<ClassifierInstance>setTrueAndFalseTrainingSets(SplittingUtilsTest.getExtendedInstances(),branch);
        Assert.assertEquals(tfPair.falseTrainingSet.size(), 6);
        Assert.assertEquals(tfPair.trueTrainingSet.size(), 2);
        for(ClassifierInstance instance : tfPair.falseTrainingSet) {
            Assert.assertTrue((Double) (instance.getAttributes().get("t")) < 6.5);        }
        for(ClassifierInstance instance : tfPair.trueTrainingSet) {
            Assert.assertTrue((Double) (instance.getAttributes().get("t")) > 6.5);        }
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