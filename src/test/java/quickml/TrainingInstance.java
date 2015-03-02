package quickml;

import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.NegativeWeightsFilter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TrainingInstance implements Instance<AttributesMap, Serializable> {

    private HashMap<String, Object> attributes;
    private AttributesMap attributesMap;
    private Double label;
    private double weight;
    private long auctionId;

    public TrainingInstance(long auctionId, HashMap<String, Object> attributes, Double classification, double weight) {
        this.auctionId = auctionId;
        this.attributes = attributes;
        this.label = classification;
        this.weight = weight;
    }

    public TrainingInstance() {
    }

    @Override
    public AttributesMap getAttributes() {
        return attributesMap;
    }

    //TODO problems deserializing
    public void convertAttributes() {
        attributesMap = new AttributesMap();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            attributesMap.put(entry.getKey(), (Serializable) entry.getValue());
        }
    }

    public Double getLabel() {
        return label;
    }

    public double getWeight() {
        return weight;
    }

    public TrainingInstance reweight(final double newWeight) {
        return new TrainingInstance(auctionId, attributes, label, newWeight);
    }

    public boolean isClick() {
        return label == 1.0D;
    }

    public long getAuctionId() {
        return auctionId;
    }

    public static void main(String[] args) {
        TrainingInstance trainingInstance = new TrainingInstance();
        Iterable<TrainingInstance> trainingInstances = Sets.newHashSet(trainingInstance);

        NegativeWeightsFilter.filterNegativeWeights(trainingInstances);
    }

}
