package quickdt;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by ian on 4/13/14.
 */
public class MiscTest {
    @Test
    public void testGetEntryWithLowestValue() throws Exception {
        Map<String, Double> map = Maps.newHashMap();
        map.put("one", 1.0);
        map.put("onepointfive", 1.5);
        map.put("zeropointfive", 0.5);
        final Optional<Map.Entry<String, Double>> entryWithLowestValue = Misc.getEntryWithLowestValue(map);
        Assert.assertTrue(entryWithLowestValue.isPresent(), "Map isn't empty so it should return a result");
        Assert.assertEquals(entryWithLowestValue.get().getKey(), "zeropointfive");
        Assert.assertEquals(entryWithLowestValue.get().getValue(), 0.5);
    }
}
