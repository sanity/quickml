package quickml.supervised.classifier;

import com.google.common.collect.Maps;
import org.apache.commons.lang.mutable.MutableInt;
import quickml.data.ClassifierInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class ClassificationProperties {
    HashMap<Serializable, MutableInt> classificationsAndCounts;

    protected ClassificationProperties(HashMap<Serializable, MutableInt> classificationsAndCounts) {
        this.classificationsAndCounts = classificationsAndCounts;
    }

    public static <T extends ClassifierInstance> ClassificationProperties getClassificationProperties(Iterable<T> trainingData) {
        HashMap<Serializable, MutableInt> classificationsAndCounts = getClassificationsAndCounts(trainingData);
        if (classificationsAndCounts.keySet().size()>2) {
            return new ClassificationProperties(classificationsAndCounts);
        }
        return BinaryClassificationProperties.createClassificationPropertiesOfBinaryData(classificationsAndCounts);
    }

    private static <T extends ClassifierInstance> HashMap<Serializable, MutableInt> getClassificationsAndCounts(Iterable<T> trainingData) {
        HashMap<Serializable, MutableInt> classificationsAndCounts = Maps.newHashMap();
        for (T instance : trainingData) {
            Serializable classification = instance.getLabel();

            if (classificationsAndCounts.containsKey(classification)) {
                classificationsAndCounts.get(classification).increment();

            } else
                classificationsAndCounts.put(classification, new MutableInt(1));

        }
        return classificationsAndCounts;
    }

    public Set<Serializable> getClassifications() {
        return classificationsAndCounts.keySet();
    }

    public boolean classificationsAreBinary(){
        return classificationsAndCounts.size() == 2;
    }
}
