package quickml.supervised.regressionModel;

import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.inspection.NumericDistributionSampler;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class SingleVariableRealValuedFunction implements PredictiveModel<Double, Double> {

    private NumericDistributionSampler attributeDistribution;
    public SingleVariableRealValuedFunction(List<Instance<Double>> attributeValues){
        attributeDistribution = new NumericDistributionSampler(attributeValues);
    }

    public abstract Double predict(Double attribute);
    public Double predictWithoutAttributes(Double attribute, Set<String> attributesToIgnore) {
        double sampledAttributeValue = sampleAttributeValue(attribute);
        return predict(sampledAttributeValue);
    }
    //this function must be implemented in all non tree based classifiers
    Double sampleAttributeValue(Double attribute) {
        return attributeDistribution.sampleDistribution();
    };
}