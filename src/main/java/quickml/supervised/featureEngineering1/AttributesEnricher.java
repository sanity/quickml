package quickml.supervised.featureEngineering1;

import com.google.common.base.Function;
import quickml.data.AttributesMap;

import java.io.Serializable;

/**
 * A Function that will take a set of attributes, and return a set of attributes that will
 * be enhanced in some way determined by the specific implementation.
 */
public interface AttributesEnricher extends Function<AttributesMap, AttributesMap>, Serializable {
}
