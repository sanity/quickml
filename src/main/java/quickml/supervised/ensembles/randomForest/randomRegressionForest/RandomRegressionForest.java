package quickml.supervised.ensembles.randomForest.randomRegressionForest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.ensembles.randomForest.RandomForest;
import quickml.supervised.tree.regressionTree.RegressionTree;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomRegressionForest implements RandomForest<Double, RegressionTree> {

    static final long serialVersionUID = 56394564395638954L;

    public final List<RegressionTree> regressionTrees;

    protected RandomRegressionForest(List<RegressionTree> regressionTrees) {
        Preconditions.checkArgument(regressionTrees.size() > 0, "We must have at least one oldTree");
        this.regressionTrees = regressionTrees;
    }

    @Override
    public Double predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        double total = 0;
        for (RegressionTree regressionTree : regressionTrees) {
            final double value = regressionTree.predictWithoutAttributes(attributes, attributesToIgnore);
            if (Double.isInfinite(value) || Double.isNaN(value)) {
                throw new RuntimeException("Probability must be a normal number, not "+value);
            }
            total += value;
        }
        return total / regressionTrees.size();
    }

    @Override
    public Double predict(AttributesMap attributes) {
        double total = 0;
        for (RegressionTree regressionTree : regressionTrees) {
            final double value = regressionTree.predict(attributes);
            if (Double.isInfinite(value) || Double.isNaN(value)) {
                throw new RuntimeException("Probability must be a normal number, not "+value);
            }
            total += value;
        }
        return total / regressionTrees.size();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RandomRegressionForest that = (RandomRegressionForest) o;

        if (!regressionTrees.equals(that.regressionTrees)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return regressionTrees.hashCode();
    }

}
