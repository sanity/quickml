package quickml.supervised.tree.scorers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import quickml.supervised.tree.decisionTree.scorers.GRPenalizedGiniImpurityScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.Arrays;

public class GiniImpurityScorerTest {
    Scorer<ClassificationCounter> scorer;

    @Before
    public void setUp(){
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        scorer = new GRPenalizedGiniImpurityScorer(0, new AttributeStats<>(null, a.add(b), "a"));
    }

    @Test
    public void sameClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("a", 4);
        Assert.assertEquals(scorer.scoreSplit(a, b), 0.0, 1E-7);
    }

    @Test
    public void diffClassificationTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        ClassificationCounter b = new ClassificationCounter();
        b.addClassification("b", 4);
        scorer = new GRPenalizedGiniImpurityScorer(0, new AttributeStats<>(Arrays.asList(a,b), a.add(b), "a"));

        Assert.assertEquals(scorer.scoreSplit(a, b), 0.5, 1E-7);
    }


    @Test
    public void parentClassificationSameAsIdenticalChildTest() {
        ClassificationCounter a = new ClassificationCounter();
        a.addClassification("a", 4);
        a.addClassification("b", 3);

        ClassificationCounter b = new ClassificationCounter();
        GRPenalizedGiniImpurityScorer scorer = new GRPenalizedGiniImpurityScorer(0, new AttributeStats<>(Arrays.asList(a,b), a.add(b), "a"));
        Assert.assertEquals(0.0, scorer.scoreSplit(a, b),  1E-7);
    }
}
