package quickml.collections;

import com.beust.jcommander.internal.Lists;
import junit.framework.TestCase;
import org.slf4j.Logger;

import java.util.List;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

public class FastApproximateSetTest extends TestCase {
    private static final  Logger logger =  getLogger(FastApproximateSetTest.class);

    public void testSpeed() {
        final int SUBSET_SIZE = 500;
        final int GLOBAL_SIZE = 1000000;
        FastApproximateSet<String> presentSet = new FastApproximateSet<>();
        List<String> globalSet = Lists.newArrayList(GLOBAL_SIZE);

        Random r = new Random();
        for (int x =0; x<100000; x++) {
            presentSet.add(Integer.toHexString(r.nextInt(GLOBAL_SIZE)));
        }
        for (int x=0; x<GLOBAL_SIZE; x++) {
            globalSet.add(Integer.toHexString(x));
        }
        {
            logger.info("Testing .contains()");
            int present = 0;
            long startTime = System.nanoTime();
            for (String s : globalSet) {
                if (presentSet.contains(s)) {
                    present++;
                }
            }
            logger.info("Found " + present + " in " + (System.nanoTime() - startTime));
        }
        {
            logger.info("Testing .probablyContains()");
            int present = 0;
            long startTime = System.nanoTime();
            for (String s : globalSet) {
                if (presentSet.probablyContains(s)) {
                    present++;
                }
            }
            logger.info("Found " + present + " in " + (System.nanoTime() - startTime));
        }
    }
}