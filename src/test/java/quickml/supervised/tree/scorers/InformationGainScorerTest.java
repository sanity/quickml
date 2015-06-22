package quickml.supervised.tree.scorers;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.tree.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

public class InformationGainScorerTest {

    @Test
    public void sameClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        InformationGainScorer scorer = new InformationGainScorer();
        Assert.assertEquals(scorer.scoreSplit(a, b), 0.0);
    }

    @Test
    public void diffClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("b", 4);
        InformationGainScorer scorer = new InformationGainScorer();
        Assert.assertEquals(scorer.scoreSplit(a, b), 1.0);
    }
}
