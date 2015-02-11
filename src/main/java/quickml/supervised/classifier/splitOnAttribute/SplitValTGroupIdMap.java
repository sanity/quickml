package quickml.supervised.classifier.splitOnAttribute;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by alexanderhawk on 2/11/15.
 */
public class SplitValTGroupIdMap extends HashMap<Serializable, Integer> {
    Integer groupId;
    public SplitValTGroupIdMap(Integer groupId){
        super();
        this.groupId = groupId;
    }

    @Override
    public Integer get(Object key) {
        return (super.get(key) != null) ? super.get(key) : groupId;
    }
}
