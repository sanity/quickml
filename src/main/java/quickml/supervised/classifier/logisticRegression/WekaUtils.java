package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.ClassifierInstance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/9/15.
 */
public class WekaUtils {

    public static SparseInstance convertClassifierInstanceToSparseInstance(HashMap<String, Integer> nameToIndexMap, ClassifierInstance instance, final Instances instances){
        int numAttr = instance.getAttributes().size()+1;
        double[] values = new double[numAttr];
        int[] indices = new int[numAttr];
        int index = 1;
        //set classification
        values[0] = (double) instance.getLabel();
        indices[0] = nameToIndexMap.get(WekaLogRegressionBuilder.LABEL);
        //set attributes
        for(Map.Entry<String, Serializable> entry : instance.getAttributes().entrySet()) {
            values[index] = (double) entry.getValue();
            indices[index] = nameToIndexMap.get(entry.getKey());
            index++;
        }
        SparseInstance sparseInstance = new SparseInstance(1.0, values, indices, nameToIndexMap.size());
        sparseInstance.setDataset(instances);
        return sparseInstance;
    }
}
