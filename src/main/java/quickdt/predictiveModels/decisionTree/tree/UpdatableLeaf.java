package quickdt.predictiveModels.decisionTree.tree;

import quickdt.data.AbstractInstance;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatableLeaf extends Leaf {
    public final Collection<Integer> trainingDataIndexes = new HashSet<>();

    public UpdatableLeaf(Node parent, Iterable<? extends AbstractInstance> instances, int depth) {
        super(parent, instances, depth);
        for(AbstractInstance instance : instances) {
            trainingDataIndexes.add(instance.index);
        }
    }

    public void addInstance(AbstractInstance instance) {
        classificationCounts.addClassification(instance.getClassification(), instance.getWeight());
        trainingDataIndexes.add(instance.index);
        exampleCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatableLeaf)) return false;
        if (!super.equals(o)) return false;

        UpdatableLeaf that = (UpdatableLeaf) o;

        if (!trainingDataIndexes.equals(that.trainingDataIndexes))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + trainingDataIndexes.hashCode();
        return result;
    }
}
