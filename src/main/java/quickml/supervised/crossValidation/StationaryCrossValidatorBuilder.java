package quickml.supervised.crossValidation;

import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;

public class StationaryCrossValidatorBuilder<R, P>  implements  CrossValidatorBuilder<R, P>{
    private CrossValLossFunction<P> lossFunction;
    private int folds = StationaryCrossValidator.DEFAULT_NUMBER_OF_FOLDS;
    private int foldsUsed =  StationaryCrossValidator.DEFAULT_NUMBER_OF_FOLDS;

    public StationaryCrossValidatorBuilder setLossFunction(CrossValLossFunction<P> lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }

    public StationaryCrossValidatorBuilder setFolds(int folds) {
        this.folds = folds;
        return this;
    }

    public StationaryCrossValidatorBuilder setFoldsUsed(int foldsUsed) {
        this.foldsUsed = foldsUsed;
        return this;
    }

    public CrossValidator<R, P> createCrossValidator() {
        return new StationaryCrossValidator(folds, foldsUsed, lossFunction);
    }
}