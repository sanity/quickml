package quickml.supervised.crossValidation;

import org.junit.Test;

import static java.util.Collections.EMPTY_LIST;

public class PredictionMapResultsTest {


    @Test(expected = IllegalArgumentException.class)
    public void testTotalLossNoData() {
        new PredictionMapResults(EMPTY_LIST);
    }



}