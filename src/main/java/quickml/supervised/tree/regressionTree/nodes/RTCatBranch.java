package quickml.supervised.tree.regressionTree.nodes;

import com.google.common.collect.Sets;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public class RTCatBranch extends Branch<MeanValueCounter>  {
    private static final long serialVersionUID = -1723969623146234761L;
    public final Set<Serializable> trueSet;

    public RTCatBranch(Branch<MeanValueCounter> parent, final String attribute, final Set<Serializable> trueSet, double probabilityOfTrueChild, double score, MeanValueCounter aggregateStats) {
        super(parent, attribute, probabilityOfTrueChild, score, aggregateStats);
        this.trueSet = Sets.newHashSet(trueSet);
    }

    @Override
    public boolean decide(final Map<String, Serializable> attributes) {
        Serializable attributeVal = attributes.get(attribute);
        //missing values always go the way of the outset...which strangely seems to be most accurate
        return trueSet.contains(attributeVal);
    }

    @Override
    public String toString() {
        return attribute + " in " + trueSet;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final RTCatBranch that = (RTCatBranch) o;

        if (!trueSet.equals(that.trueSet)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + trueSet.hashCode();
        return result;
    }
}
