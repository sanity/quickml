package quickml.supervised.tree.scorers;

import org.junit.Before;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.tree.decisionTree.scorers.GRPenalizedGiniImpurityScorer;
import quickml.supervised.tree.decisionTree.scorers.PenalizedMSEScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

/**
 * Created by ian on 2/27/14.
 */
public class PenalizedMSEScorerTest {

    Scorer<ClassificationCounter> mseScorer;

    @Before
    public void setUp(){
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        mseScorer = new PenalizedMSEScorer(0, 0, new AttributeStats<>(null, a.add(b), "a"));
    }

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
        Assert.assertTrue(Math.abs(mseScorer.scoreSplit(a, b)- 0.021776929) < 0.000000001);
    }
}
