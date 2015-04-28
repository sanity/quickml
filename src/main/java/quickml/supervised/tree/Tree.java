package quickml.supervised.tree;

import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModel;

/**
 * Created by alexanderhawk on 4/3/15.
 */

//do i need to reference P when making TR a generic that extends Tree?  TR extends Tree<?>  meaning TR can be any implementation whose return types are anything, but input types are basically objects
    //so need L, TR extends Tree<L> for TR to refer the inheritance hieranchy of Tree
public interface Tree<P> extends PredictiveModel<AttributesMap, P> {

}
