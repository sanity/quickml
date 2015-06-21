package quickml.supervised.tree.decisionTree.nodes;

import com.google.common.collect.Sets;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;

import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public class DTCatBranch extends DTBranch  {
    private static final long serialVersionUID = -1723969623146234761L;
    public final Set<Object> inSet;

    public DTCatBranch(Branch<ClassificationCounter, DTNode> parent, final String attribute, final Set<Object> inSet, double probabilityOfTrueChild, double score, ClassificationCounter aggregateStats ) {
        super(parent, attribute, probabilityOfTrueChild, score, aggregateStats);
        this.inSet = Sets.newHashSet(inSet);
    }

    @Override
    public boolean decide(final Map<String, Object> attributes) {
        Object attributeVal = attributes.get(attribute);
        //missing values always go the way of the outset...which strangely seems to be most accurate
        return inSet.contains(attributeVal);
    }

    @Override
    public String toString() {
        return attribute + " in " + inSet;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final DTCatBranch that = (DTCatBranch) o;

        if (!inSet.equals(that.inSet)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + inSet.hashCode();
        return result;
    }
}
