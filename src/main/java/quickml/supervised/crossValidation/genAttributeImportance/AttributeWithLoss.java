package quickml.supervised.crossValidation.genAttributeImportance;

public class AttributeWithLoss implements Comparable<AttributeWithLoss> {
    private String attribute;
    private double loss;

    public AttributeWithLoss(String attribute, double loss) {
        this.attribute = attribute;
        this.loss = loss;
    }

    // Compare the other loss to this objects loss, we want the attributes with the
    // highest loss to come first (since removing them has the biggest affect on loss)
    @Override
    public int compareTo(AttributeWithLoss o) {
        return Double.compare(o.loss, loss);
    }

    public String getAttribute() {
        return attribute;
    }

    public double getLoss() {
        return loss;
    }
}
