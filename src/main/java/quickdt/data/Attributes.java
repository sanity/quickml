package quickdt.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 6/27/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Attributes extends Map<String, Serializable> {
    public Instance classification(Serializable cls);
}
