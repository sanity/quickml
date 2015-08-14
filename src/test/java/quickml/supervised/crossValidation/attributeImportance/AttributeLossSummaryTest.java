package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AttributeLossSummaryTest {

    private AttributeLossSummary attributeLossSummary;

    private ClassifierLossFunction lossFunction = new ClassifierRMSELossFunction();
    private AttributeLossTracker lossTracker1;
    private AttributeLossTracker lossTracker2;
    private AttributeLossTracker lossTracker3;


    @Before
    public void setUp() throws Exception {
        lossTracker1 = new MockAttributeLossTracker(0.6, newHashSet("a", "b", "c"));
        lossTracker2 = new MockAttributeLossTracker(0.2, newHashSet("a", "b"));
        lossTracker3 = new MockAttributeLossTracker(0.4, newHashSet("a", "b", "c", "d", "e"));

        attributeLossSummary = new AttributeLossSummary(newArrayList(lossTracker1, lossTracker2, lossTracker3));
    }

    @Test
    public void testGetOptimalAttributesReturnsTrackerWithLowestLoss() throws Exception {
        assertEquals(2, attributeLossSummary.getOptimalAttributes().size());
        assertTrue(attributeLossSummary.getOptimalAttributes().contains("a"));
        assertTrue(attributeLossSummary.getOptimalAttributes().contains("b"));
    }

    @Test
    public void testgetMaximalAttributes() throws Exception {
        assertEquals(5, attributeLossSummary.getMaximalSet(5).size());
        assertEquals(5, attributeLossSummary.getMaximalSet(4).size());
        assertEquals(2, attributeLossSummary.getMaximalSet(2).size());

    }

    // Simple mock class to override loss and attributes
    class MockAttributeLossTracker extends AttributeLossTracker {

        private double loss;
        private Set<String> attributes;

        public MockAttributeLossTracker(double loss, Set<String> attributes) {
            super(attributes, newArrayList(lossFunction), lossFunction);
            this.loss = loss;
            this.attributes = attributes;
        }

        @Override
        public double getOverallLoss() {
            return loss;
        }

        @Override
        public List<String> getOrderedAttributes() {
            return Lists.newArrayList(attributes);
        }
    }

}