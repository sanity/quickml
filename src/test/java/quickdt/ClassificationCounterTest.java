package quickdt;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by ian on 2/27/14.
 */
public class ClassificationCounterTest {
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
