package quickml.data;


import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 6/27/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Instance<R> {
    public abstract R getAttributes();

    public abstract Serializable getLabel();

    public abstract double getWeight();

    public abstract Instance<R>reweight(double newWeight);
}
