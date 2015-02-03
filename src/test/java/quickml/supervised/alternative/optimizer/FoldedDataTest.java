package quickml.supervised.alternative.optimizer;

import com.beust.jcommander.internal.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class FoldedDataTest {


    private List<Integer> instances;

    @Before
    public void setUp() throws Exception {
        instances = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void testCycleThrough5Of10Folds() throws Exception {
        FoldedData<Integer> foldedData = new FoldedData<>(instances, 10, 5);

        assertEquals(new Integer(1), foldedData.getValidationSet().get(0));
        foldedData.nextCycle();

        assertEquals(new Integer(2), foldedData.getValidationSet().get(0));
        foldedData.nextCycle();

        assertEquals(new Integer(3), foldedData.getValidationSet().get(0));
        foldedData.nextCycle();

        assertEquals(new Integer(4), foldedData.getValidationSet().get(0));
        foldedData.nextCycle();

        assertEquals(new Integer(5), foldedData.getValidationSet().get(0));
        foldedData.nextCycle();


        assertEquals(9, foldedData.getTrainingSet().size());

        assertFalse(foldedData.hasMore());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNumFoldsIsZero() throws Exception {
        new FoldedData<>(instances, 0, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFoldsUsedIs0() throws Exception {
        new FoldedData<>(instances, 4, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFoldsUsedMustBeLessThanFolds() throws Exception {
        new FoldedData<>(instances, 2, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoEmptySet() throws Exception {
        new FoldedData<>(Lists.newArrayList(), 0, 0);
    }
}