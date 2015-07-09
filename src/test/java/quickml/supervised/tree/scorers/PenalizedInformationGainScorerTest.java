package quickml.supervised.tree.scorers;

import org.junit.Before;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.tree.decisionTree.scorers.GRPenalizedGiniImpurityScorer;
import quickml.supervised.tree.decisionTree.scorers.PenalizedInformationGainScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

public class PenalizedInformationGainScorerTest {
    Scorer<ClassificationCounter> scorer;

    @Before
    public void setUp(){
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        scorer = new PenalizedInformationGainScorer(0, 0, new AttributeStats<>(null, a.add(b), "a"));
    }

    @Test
    public void sameClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        Assert.assertEquals(scorer.scoreSplit(a, b), 0.0);
    }

    @Test
    public void diffClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("b", 4);
        Assert.assertEquals(scorer.scoreSplit(a, b), 1.0);
    }
}
