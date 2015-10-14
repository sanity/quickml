package quickml.data;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public interface InstanceFactory<I, A, L> {
    I createInstance(A attributes, L label, double weight);
}
