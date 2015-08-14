package quickml.supervised.rankingModels;

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

    public Set<Serializable> getItemsWithOutcomes() {
        return itemToOutcome.keySet();
    }

    public Collection<Double> getOutcomes() {
        return itemToOutcome.values();
    }
}
