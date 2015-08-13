package quickml.supervised.rankingModels;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class RankingPrediction {
    private List<Serializable> rankedItems = Lists.newArrayList();
    private Map<Serializable, Integer> itemsToRanks = Maps.newHashMap();

    public RankingPrediction(List<Serializable> rankedItems) {
        this.rankedItems = rankedItems;
        for (int i = 0; i<rankedItems.size(); i++) {
            Serializable item = rankedItems.get(i);
            itemsToRanks.put(item, i+1);
        }
    }
    public List<Serializable> getRankOrder(){
        return rankedItems;
    }

    public int getRankOfItem(Serializable item){
        return itemsToRanks.get(item);
    }
}
