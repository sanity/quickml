package quickdt.scorers;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.ClassificationCounter;

/**
 * Created by ian on 2/27/14.
 */
public class MSEScorerTest {
    @Test
    public void simpleTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        a.addClassification("b", 9);
        a.addClassification("c", 1);
        ClassificationCounter b = new ClassificationCounter();
       b.addClassification("a", 5);
       b.addClassification("b", 9);
       b.addClassification("c", 6);
        MSEScorer mseScorer = new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE);
        Assert.assertTrue(Math.abs(mseScorer.scoreSplit(a, b)- 0.021776929) < 0.000000001);
    }
}
