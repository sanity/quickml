package quickml.supervised.tree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.scorers.Scorer;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/22/15.
 */
public class ParentOfRoot<TS extends ValueCounter<TS>> extends Branch<TS> {
    public ParentOfRoot() {
        super(null, "parentOfRoot", 0, Scorer.NO_SCORE, null);
    }

    @Override
    public boolean decide(Map<String, Object> attributes) {
        return false;
    }

    @Override
    public Leaf<TS> getLeaf(AttributesMap attributes) {
        return null;
    }
}
