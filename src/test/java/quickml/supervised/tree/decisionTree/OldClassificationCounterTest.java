package quickml.supervised.tree.decisionTree;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

/**
 * Created by ian on 2/27/14.
 */
public class OldClassificationCounterTest {

    @Test
    public void testAdd() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("dog", 1.0);
        a.addClassification("cat", 0.5);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("dog", 0.5);
        b.addClassification("cat", 1.0);
        ClassificationCounter c = a.add(b);
        Assert.assertEquals(c.getCount("dog"), 1.5);
        Assert.assertEquals(c.getCount("cat"), 1.5);
    }

    @Test
    public void testSubtract() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("dog", 1.0);
        a.addClassification("cat", 2.5);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("dog", 0.5);
        b.addClassification("cat", 1.0);
        ClassificationCounter c = a.subtract(b);
        Assert.assertEquals(c.getCount("dog"), 0.5);
        Assert.assertEquals(c.getCount("cat"), 1.5);
    }

    @Test
    public void testMerge() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("dog", 1.0);
        a.addClassification("cat", 0.5);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("dog", 0.5);
        b.addClassification("cat", 1.0);
        ClassificationCounter merged = ClassificationCounter.merge(a, b);
        Assert.assertEquals(merged.getTotal(), 3.0);
        Assert.assertEquals(merged.getCount("dog"), 1.5);
        Assert.assertEquals(merged.getCount("cat"), 1.5);
    }
}
