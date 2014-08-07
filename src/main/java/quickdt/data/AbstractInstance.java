package quickdt.data;


import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 6/27/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractInstance<R> {
    public abstract R getRegressors();

    public abstract Serializable getLabel();

    public abstract double getWeight();

    public abstract AbstractInstance reweight(double newWeight);

    public int index;
}
