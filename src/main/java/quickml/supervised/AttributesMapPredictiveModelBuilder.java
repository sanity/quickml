package quickml.supervised;

import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public interface AttributesMapPredictiveModelBuilder<P, PM extends PredictiveModel<AttributesMap, P>, L, I extends InstanceWithAttributesMap<L>>  extends PredictiveModelBuilder<P, AttributesMap, PM, L, I> {
}
