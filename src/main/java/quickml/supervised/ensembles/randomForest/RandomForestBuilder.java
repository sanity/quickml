package quickml.supervised.ensembles.randomForest;

import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.tree.Tree;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public abstract class RandomForestBuilder<P, PM extends RandomForest<P, ? extends Tree<P>>, I extends InstanceWithAttributesMap<?>> implements PredictiveModelBuilder<AttributesMap, PM, I> {
    protected int numTrees = 8;


    public abstract PM buildPredictiveModel(Iterable<I> trainingData);

    public int getNumTrees() {
        return numTrees;
    }
}
