package quickdt.predictiveModels.decisionTree.tree;

import quickdt.data.Instance;


import quickdt.data.Instance;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatableLeaf extends Leaf {
    private final Collection<Instance> instances = new HashSet<>();


    public UpdatableLeaf(Node parent, Iterable<Instance<Map<String, Serializable>>> instances, int depth) {
        super(parent, instances, depth);
        for(Instance instance : instances) {
            this.instances.add(instance);
        }
    }

    public void addInstance(Instance instance) {
        classificationCounts.addClassification(instance.getLabel(), instance.getWeight());
        instances.add(instance);
        exampleCount++;
    }

    public Collection<Instance> getInstances() {
        return instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatableLeaf)) return false;
        if (!super.equals(o)) return false;

        UpdatableLeaf that = (UpdatableLeaf) o;

        if (!instances.equals(that.instances))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + instances.hashCode();
        return result;
    }
}
