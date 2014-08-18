package quickml.supervised.collaborativeFiltering.gradientDescent;

import org.apache.commons.lang.NotImplementedException;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.collaborativeFiltering.UserItem;

import java.io.Serializable;

/**
 * An implementation of this http://sifter.org/~simon/journal/20061211.html
 */
public class GradientDescentCFBuilder implements PredictiveModelBuilder<UserItem, GradientDescentCF> {
    private double learningRate = 0.001;

    public GradientDescentCFBuilder learningRate(double learningRate) {
        this.learningRate = learningRate;
        return this;
    }

    @Override
    public GradientDescentCF buildPredictiveModel(final Iterable<Instance<UserItem>> trainingData) {
        return null;
    }

    @Override
    public PredictiveModelBuilder<UserItem, GradientDescentCF> updatable(final boolean updatable) {
        throw new NotImplementedException();
    }

    @Override
    public void setID(final Serializable id) {

    }
}
