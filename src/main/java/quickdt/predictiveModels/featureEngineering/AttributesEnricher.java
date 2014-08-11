package quickdt.predictiveModels.featureEngineering;

import com.google.common.base.Function;

import java.io.Serializable;
import java.util.Map;

/**
 * A Function that will take a set of attributes, and return a set of attributes that will
 * be enhanced in some way determined by the specific implementation.
 */
public interface AttributesEnricher extends Function<Map<String, Serializable>, Map<String, Serializable>>, Serializable {
}
