package quickdt.predictiveModels.decisionTree.scorers;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.predictiveModels.decisionTree.tree.ClassificationCounter;

public class GiniImpurityScorerTest {
    @Test
    public void sameClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        GiniImpurityScorer scorer = new GiniImpurityScorer();
        Assert.assertEquals(scorer.scoreSplit(a, b), 0.0);
    }

    @Test
    public void diffClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("b", 4);
        GiniImpurityScorer scorer = new GiniImpurityScorer();
        Assert.assertEquals(scorer.scoreSplit(a, b), 0.5);
    }
}
