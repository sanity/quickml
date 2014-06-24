package quickdt.predictiveModels.decisionTree.scorers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.javatuples.Pair;
import quickdt.predictiveModels.decisionTree.Scorer;
import quickdt.predictiveModels.decisionTree.tree.ClassificationCounter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class CachingScorer implements Scorer {
    LoadingCache<Pair, Double> scores;

    public CachingScorer(final Scorer scorer, int maxEntries) {
        scores = CacheBuilder.newBuilder().maximumSize(maxEntries).build(new CacheLoader<Pair, Double>() {
            @Override
            public Double load(Pair key) throws Exception {
                return scorer.scoreSplit((ClassificationCounter)key.getValue0(), (ClassificationCounter)key.getValue1());
            }
        });
    }

    @Override
    public double scoreSplit(ClassificationCounter a, ClassificationCounter b) {
        Pair pair = Pair.with(a, b);
        try {
            return scores.get(pair);
        } catch (ExecutionException e) {
            return Double.MIN_VALUE;
        }
    }
}
