package quickdt.predictiveModels.featureEngineering;

import com.google.common.base.Function;
import quickdt.data.Attributes;

import java.io.Serializable;

/**
 * Created by ian on 5/20/14.
 */
public interface AttributesEnricher extends Function<Attributes, Attributes>, Serializable {
}
