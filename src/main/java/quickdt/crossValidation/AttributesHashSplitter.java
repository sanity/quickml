package quickdt.crossValidation;

import com.google.common.base.Predicate;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import quickdt.data.AbstractInstance;

/**
* Created by ian on 2/28/14.
*/
public class AttributesHashSplitter implements Predicate<AbstractInstance> {

    private static final HashFunction hashFunction = Hashing.murmur3_32();

    private final int every;

    public AttributesHashSplitter(int every) {
        this.every = every;
    }

    @Override
    public boolean apply(final AbstractInstance instance) {
        int hc = hashFunction.hashInt(instance.getAttributes().hashCode()).asInt();
        return Math.abs(hc) % every == 0;
    }
}
