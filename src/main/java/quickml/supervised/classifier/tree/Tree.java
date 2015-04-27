package quickml.supervised.classifier.tree;

import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModel;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 4/3/15.
 */

//do i need to reference P when making TR a generic that extends Tree?  TR extends Tree<?>  meaning TR can be any implementation whose return types are anything, but input types are basically objects
public interface Tree<P> extends PredictiveModel<AttributesMap, P> {

}
