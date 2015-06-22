package quickml.supervised;

import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public interface AttributesMapPredictiveModelBuilder<PM extends PredictiveModel<AttributesMap, ?>, I extends InstanceWithAttributesMap<?>>  extends PredictiveModelBuilder<AttributesMap, PM, I> {
}
