package quickml.supervised.ensembles.randomForest;

import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.AttributesMapPredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.tree.Tree;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public abstract class RandomForestBuilder<P, R extends RandomForest<P, ? extends Tree<P>>, L, I extends InstanceWithAttributesMap<L>> implements AttributesMapPredictiveModelBuilder<P, R, L, I> {
    protected int numTrees = 8;
    public abstract R buildPredictiveModel(Iterable<I> trainingData);

    public int getNumTrees() {
        return numTrees;
    }
}
