package quickml.supervised.crossValidation.crossValLossFunctions;



import java.util.List;

/**
 * Created by alexanderhawk on 4/24/14.
 */
public interface CrossValLossFunction<P>  {
    double getLoss(List<LabelPredictionWeight<P>> labelPredictionWeights);
}
