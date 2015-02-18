package quickml.supervised.crossValidation.utils;

import com.google.common.base.Predicate;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
* Created by ian on 2/28/14.
*/
public class AttributesHashSplitter implements Predicate<Instance<AttributesMap, Serializable>> {

    private static final HashFunction hashFunction = Hashing.murmur3_32();

    private final int every;

    public AttributesHashSplitter(int every) {
        this.every = every;
    }

    @Override
    public boolean apply(final Instance<AttributesMap, Serializable> instance) {
        int hc = hashFunction.hashInt(instance.getAttributes().hashCode()).asInt();
        return Math.abs(hc) % every == 0;
    }
}
