package quickdt.data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 6/27/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractInstance {
    public abstract Attributes getAttributes();

    public abstract Serializable getClassification();

    public abstract double getWeight();

    public abstract AbstractInstance reweight(double newWeight);

    public int index;
}
