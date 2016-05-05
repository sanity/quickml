package quickml.supervised.tree.scorers;

import org.junit.Assert;

import org.junit.Test;
import quickml.supervised.tree.decisionTree.scorers.PenalizedMSEScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.Arrays;

/**
 * Created by ian on 2/27/14.
 */
public class PenalizedMSEScorerTest {

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
        PenalizedMSEScorer mseScorer = new PenalizedMSEScorer(0, 0.0, new AttributeStats<>(Arrays.asList(a, b), a.add(b), "a"));

        Assert.assertTrue(Math.abs(mseScorer.scoreSplit(a, b) - 0.021776929) < 0.000000001);
    }
}
