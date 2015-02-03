package quickml.supervised.crossValidation.crossValLossFunctions;



import java.util.List;

/**
 * Created by alexanderhawk on 4/24/14.
 */
public interface CrossValLossFunction<L, P>  {
    double getLoss(List<LabelPredictionWeight<L, P>> labelPredictionWeights);
}
