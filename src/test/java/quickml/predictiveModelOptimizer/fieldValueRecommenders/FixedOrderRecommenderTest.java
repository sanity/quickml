package quickml.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

/**
 * Created by ian on 4/12/14.
 */
public class FixedOrderRecommenderTest {
    @Test
    public void simpleTest() {
        FixedOrderRecommender fixedOrderRecommender = new FixedOrderRecommender(1, 2, 3);
        Assert.assertEquals(fixedOrderRecommender.recommendNextValue(Collections.<Object, Double>emptyMap()), Optional.of(1));
        Map<Object, Double> oneDone = Maps.newHashMap();
        oneDone.put(1, 0.0);
        Assert.assertEquals(fixedOrderRecommender.recommendNextValue(oneDone), Optional.of(2));
    }
}
