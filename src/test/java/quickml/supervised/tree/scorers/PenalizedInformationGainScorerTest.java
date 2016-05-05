package quickml.supervised.tree.scorers;

import org.junit.Assert;

import org.junit.Test;
import quickml.supervised.tree.decisionTree.scorers.PenalizedInformationGainScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.Arrays;

public class PenalizedInformationGainScorerTest {

    @Test
    public void sameClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        PenalizedInformationGainScorer scorer = new PenalizedInformationGainScorer(0, 0.0, new AttributeStats<>(Arrays.asList(a, b), a.add(b), "a"));

        Assert.assertEquals(scorer.scoreSplit(a, b), 0.0, 1E-7);
    }

    @Test
    public void diffClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("b", 4);
        PenalizedInformationGainScorer scorer = new PenalizedInformationGainScorer(0, 0.0, new AttributeStats<>(Arrays.asList(a, b), a.add(b), "a"));

        Assert.assertEquals(scorer.scoreSplit(a, b), 1.0, 1E-7);
    }
}
