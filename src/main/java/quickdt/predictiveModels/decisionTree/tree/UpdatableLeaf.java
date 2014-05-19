package quickdt.predictiveModels.decisionTree.tree;

import quickdt.data.AbstractInstance;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatableLeaf extends Leaf {
    public final Collection<AbstractInstance> instances = new LinkedList<>();

    public UpdatableLeaf(Node parent, Iterable<? extends AbstractInstance> instances, int depth) {
        super(parent, instances, depth);
        for(AbstractInstance instance : instances) {
            this.instances.add(instance);
        }
    }

    public void addInstance(AbstractInstance instance) {
        classificationCounts.addClassification(instance.getClassification(), instance.getWeight());
        this.instances.add(instance);
        exampleCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatableLeaf)) return false;
        if (!super.equals(o)) return false;

        UpdatableLeaf that = (UpdatableLeaf) o;

        if (!instances.equals(that.instances)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + instances.hashCode();
        return result;
    }
}
