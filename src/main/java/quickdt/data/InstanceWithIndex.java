package quickdt.data;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 8/11/14.
 */
public final class InstanceWithIndex<R> extends InstanceImpl<R>{
    private final int index;

    public InstanceWithIndex(final R attributes, final Serializable label, final double weight, int index) {
        super(attributes, label, weight);
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

}
