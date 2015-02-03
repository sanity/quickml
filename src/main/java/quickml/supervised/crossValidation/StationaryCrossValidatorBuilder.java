package quickml.supervised.crossValidation;

import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;

//TODO[mk] fix this
public class StationaryCrossValidatorBuilder<R,L, P>  implements  CrossValidatorBuilder<R, L, P>{
    private CrossValLossFunction<L, P> lossFunction;
    private int folds = StationaryCrossValidator.DEFAULT_NUMBER_OF_FOLDS;
    private int foldsUsed =  StationaryCrossValidator.DEFAULT_NUMBER_OF_FOLDS;

    public StationaryCrossValidatorBuilder setLossFunction(CrossValLossFunction<L, P> lossFunction) {
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

    public CrossValidator<R,L, P> createCrossValidator() {
//        return new StationaryCrossValidator(folds, foldsUsed, lossFunction);
        return null;
    }
}