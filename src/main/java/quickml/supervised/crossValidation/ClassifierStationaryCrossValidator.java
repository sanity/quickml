package quickml.supervised.crossValidation;

import com.google.common.base.Preconditions;
import quickml.data.MapWithDefaultOfZero;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class ClassifierStationaryCrossValidator extends StationaryCrossValidator<Map<String, Serializable>, MapWithDefaultOfZero>{
    public ClassifierStationaryCrossValidator(CrossValLossFunction<MapWithDefaultOfZero> lossFunction) {
        super(DEFAULT_NUMBER_OF_FOLDS, lossFunction);
    }

    public ClassifierStationaryCrossValidator(final int folds, CrossValLossFunction<MapWithDefaultOfZero> lossFunction) {
        super(folds, folds, lossFunction);

    }

    public ClassifierStationaryCrossValidator(final int folds, final int foldsUsed, CrossValLossFunction<MapWithDefaultOfZero> lossFunction) {
        super(folds, foldsUsed, lossFunction);
    }

}
