package quickdt;

import java.io.Serializable;

public class BaggingResult {
    
    public Serializable classification;
    public double confidence;
    
    /**
     * @param classification
     * @param confidence
     */
    public BaggingResult(Serializable classification, double confidence) {
        this.classification = classification;
        this.confidence = confidence;
    }

}
