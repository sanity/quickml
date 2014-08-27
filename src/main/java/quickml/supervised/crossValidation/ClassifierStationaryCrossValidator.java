package quickml.supervised.crossValidation;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class ClassifierStationaryCrossValidator extends StationaryCrossValidator<AttributesMap, PredictionMap>{
    public ClassifierStationaryCrossValidator(CrossValLossFunction<PredictionMap> lossFunction) {
        super(DEFAULT_NUMBER_OF_FOLDS, lossFunction);
    }

    public ClassifierStationaryCrossValidator(final int folds, CrossValLossFunction<PredictionMap> lossFunction) {
        super(folds, folds, lossFunction);

    }

    public ClassifierStationaryCrossValidator(final int folds, final int foldsUsed, CrossValLossFunction<PredictionMap> lossFunction) {
        super(folds, foldsUsed, lossFunction);
    }

}
