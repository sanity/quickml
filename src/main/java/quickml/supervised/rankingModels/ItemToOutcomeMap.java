package quickml.supervised.rankingModels;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class ItemToOutcomeMap implements Serializable {
    public HashMap<Serializable, Double> itemToOutcome;

    public ItemToOutcomeMap(HashMap<Serializable, Double> itemToOutcome) {
        this.itemToOutcome = itemToOutcome;
    }

    public Iterator<Map.Entry<Serializable,Double>> iterator(){
        return itemToOutcome.entrySet().iterator();
    }

    public double getOutcome(Serializable item) {
        return itemToOutcome.get(item);
    }

    public List<Serializable> getItems() {
        return Lists.newArrayList(itemToOutcome.keySet());
    }

    public Serializable getFirstItem(){
        Iterator<Serializable> items = itemToOutcome.keySet().iterator();
        if (items.hasNext()) {
            return items.next();
        }
        else{
            return null;
        }
    }

    public int size() {
        return itemToOutcome.size();
    }

    public Collection<Double> getOutcomes() {
        return itemToOutcome.values();
    }
}
