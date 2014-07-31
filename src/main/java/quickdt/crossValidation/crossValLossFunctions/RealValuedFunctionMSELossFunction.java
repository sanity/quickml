package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.RealValuedFunction;

import java.util.List;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class RealValuedFunctionMSELossFunction<R extends RealValuedFunction> implements CrossValLossFunction<R> {
    double totalLoss = 0;
    double weightOfAllInstances = 0;

    @Override
    public double getLoss(List<? extends AbstractInstance> crossValSet, R realValuedFunction) {

        for (AbstractInstance instance : crossValSet) {
            double predictedValueOfInstance = realValuedFunction.predict(instance.getAttributes()).getPrediction();
            totalLoss += Math.pow((Double) instance.getLabel() - predictedValueOfInstance, 2) * instance.getWeight();
            weightOfAllInstances += instance.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }
}

