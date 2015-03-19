package quickml.supervised.classifier;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.mutable.MutableInt;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class BinaryClassificationProperties extends ClassificationProperties {

    public Serializable minorityClassification;
    public Serializable majorityClassification;
    public double majorityToMinorityRatio = 1;

    protected BinaryClassificationProperties(HashMap<Serializable, MutableInt> classificationsAndCounts, Serializable minorityClassification, Serializable majorityClassification, double majorityToMinorityRatio) {
        super(classificationsAndCounts);
        this.minorityClassification = minorityClassification;
        this.majorityClassification = majorityClassification;
        this.majorityToMinorityRatio = majorityToMinorityRatio;
    }


    protected static ClassificationProperties createClassificationPropertiesOfBinaryData(HashMap<Serializable, MutableInt> classificationsAndCounts) {
        Preconditions.checkArgument(classificationsAndCounts.keySet().size() ==2);
        Serializable minorityClassification = null;
        Serializable majorityClassification = null;
        boolean binaryClassifications = true;
        double majorityToMinorityRatio;
        double minorityClassificationCount = 0;

        double majorityClassificationCount = 0;
        for (Serializable val : classificationsAndCounts.keySet()) {
            if (majorityClassification == null || classificationsAndCounts.get(val).doubleValue() > majorityClassificationCount) {
                majorityClassification = val;
                majorityClassificationCount = classificationsAndCounts.get(val).doubleValue();
            }
            else if (minorityClassification == null || classificationsAndCounts.get(val).doubleValue() < minorityClassificationCount) {
                minorityClassification = val;
                minorityClassificationCount = classificationsAndCounts.get(val).doubleValue();
            }

        }
        majorityToMinorityRatio = classificationsAndCounts.get(majorityClassification).doubleValue()
                / classificationsAndCounts.get(minorityClassification).doubleValue();

        return new BinaryClassificationProperties(classificationsAndCounts, minorityClassification, majorityClassification,majorityToMinorityRatio);
    }

}

