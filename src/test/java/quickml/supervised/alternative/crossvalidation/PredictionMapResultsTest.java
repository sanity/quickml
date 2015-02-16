package quickml.supervised.alternative.crossvalidation;

import org.junit.Test;
import quickml.supervised.crossValidation.PredictionMapResults;

import static java.util.Collections.EMPTY_LIST;

public class PredictionMapResultsTest {


    @Test(expected = IllegalArgumentException.class)
    public void testTotalLossNoData() {
        new PredictionMapResults(EMPTY_LIST);
    }



}