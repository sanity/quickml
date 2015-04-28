package quickml.supervised.tree.terminationConditions;

public class StandardTerminationConditionsBuilder {
    private double minScore;
    private int maxDepth;
    private int minLeafInstances;

    public StandardTerminationConditionsBuilder minScore(double minScore) {
        this.minScore = minScore;
        return this;
    }

    public StandardTerminationConditionsBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public StandardTerminationConditionsBuilder minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public StandardTerminationConditions buildStandardTerminationConditions() {
        return new StandardTerminationConditions(minScore, maxDepth, minLeafInstances);
    }
}