package quickml.supervised.ensembles.randomForest;

import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.tree.Tree;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public interface RandomForest<P, TR extends Tree<P>> extends PredictiveModel<AttributesMap, P> {
}
