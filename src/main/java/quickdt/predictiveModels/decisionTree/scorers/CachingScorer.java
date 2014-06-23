package quickdt.predictiveModels.decisionTree.scorers;

import org.javatuples.Pair;
import quickdt.predictiveModels.decisionTree.Scorer;
import quickdt.predictiveModels.decisionTree.tree.ClassificationCounter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class CachingScorer implements Scorer {
    Map<Pair, Double> scores = new HashMap<>();

    private final Scorer scorer;
    private final int maxEntries;

    public CachingScorer(Scorer scorer, int maxEntries) {
        this.scorer = scorer;
        this.maxEntries = maxEntries;
    }

    @Override
    public double scoreSplit(ClassificationCounter a, ClassificationCounter b) {
        Pair pair = Pair.with(a, b);
        Double score = scores.get(pair);
        if (score == null) {
            score = scorer.scoreSplit(a, b);
            scores.put(pair, score);
            if (scores.size() > maxEntries) {
                scores.clear();
            }
        }
        return score;
    }
}
