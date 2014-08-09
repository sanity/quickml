package quickdt.crossValidation;

import com.google.common.base.Predicate;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import quickdt.data.AbstractInstance;

import java.io.Serializable;
import java.util.Map;

/**
* Created by ian on 2/28/14.
*/
public class AttributesHashSplitter implements Predicate<AbstractInstance<Map<String, Serializable>>> {

    private static final HashFunction hashFunction = Hashing.murmur3_32();

    private final int every;

    public AttributesHashSplitter(int every) {
        this.every = every;
    }

    @Override
    public boolean apply(final AbstractInstance<Map<String, Serializable>> instance) {
        int hc = hashFunction.hashInt(instance.getRegressors().hashCode()).asInt();
        return Math.abs(hc) % every == 0;
    }
}
