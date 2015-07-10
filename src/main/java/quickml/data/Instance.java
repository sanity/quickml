package quickml.data;


import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 6/27/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Instance<A, L> {

    public A getAttributes(); // TODO rename to getInput

    public L getLabel(); // TODO rename to getOuput

    public double getWeight();

}
