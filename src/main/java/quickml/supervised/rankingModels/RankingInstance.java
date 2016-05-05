package quickml.supervised.rankingModels;

import quickml.data.AttributesMap;
import quickml.data.instances.InstanceWithAttributesMap;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class RankingInstance extends InstanceWithAttributesMap<ItemToOutcomeMap> {
    /**the label for a list of recs is a HashMap of items (serializables) to outcome values */

    public RankingInstance(AttributesMap attributes, ItemToOutcomeMap label) {
        super(attributes, label, 1.0);
    }

    public RankingInstance(AttributesMap attributes, ItemToOutcomeMap label, double weight) {
        super(attributes, label, weight);
    }
    public Serializable getFirstItem(){
        return getLabel().getFirstItem();
    }

}


