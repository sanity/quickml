package quickdt.inspection;

/**
 * Created by ian on 3/29/14.
 */
public class AttributeScore implements Comparable<AttributeScore> {
    private final String attribute;
    private final double score;

    public AttributeScore(final String attribute, final double score) {
        this.attribute = attribute;
        this.score = score;
    }

    @Override
    public int compareTo(final AttributeScore o) {
        return Double.compare(score, o.score);
    }

    public String getAttribute() {
        return attribute;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AttributeScore{");
        sb.append("attribute='").append(attribute).append('\'');
        sb.append(", score=").append(score);
        sb.append('}');
        return sb.toString();
    }
}
