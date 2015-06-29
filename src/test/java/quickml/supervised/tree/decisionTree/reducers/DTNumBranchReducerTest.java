package quickml.supervised.tree.decisionTree.reducers;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.branchFinders.SplittingUtilsTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 6/25/15.
 */
public class DTNumBranchReducerTest {

    @Test
    public void getDeterministicSplitTest2() {
        List<ClassifierInstance> instances = SplittingUtilsTest.getExtendedInstances();
        String attribute = "t";
        int numNumericBins = 6;
//        List<I> instances, String, attribute, int numNumericBins) {
        Assert.assertEquals(0, 0);

    }

    @Test
    public void allValuesSameTest(){
        double[] x = {0, 0, 0, 0};
        Assert.assertTrue(DTNumBranchReducer.allValuesSame(x));

        double[] y = {0, 0, 0, 1};
        Assert.assertTrue(!DTNumBranchReducer.allValuesSame(y));
    }

    @Test
    public void getBinDividerPointsTest(){
        List<Double> valuesList = Arrays.<Double>asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Optional<double[]> splits = DTNumBranchReducer.getBinDividerPoints(4, valuesList);
        Assert.assertEquals(splits.get()[0], 2.5, 1E-5);

        valuesList = Arrays.<Double>asList(1.0, 2.0, 3.0, 4.0);
        splits = DTNumBranchReducer.getBinDividerPoints(4, valuesList);
        Assert.assertEquals(splits.get()[0], 1.5, 1E-5);

        valuesList = Arrays.<Double>asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
        splits = DTNumBranchReducer.getBinDividerPoints(4, valuesList);
        Assert.assertEquals(splits.get()[0], 2.5, 1E-5);

        valuesList = Arrays.<Double>asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        splits = DTNumBranchReducer.getBinDividerPoints(4, valuesList);
        Assert.assertEquals(splits.get()[2], 6.5, 1E-5);
    }

    @Test
    public void getDeterministicSplitTest(){
        List<ClassifierInstance> instances = SplittingUtilsTest.getInstances();
        Optional<double[]> splits =DTNumBranchReducer.<ClassifierInstance>getDeterministicSplit(instances, "t", 4);
        Assert.assertEquals(splits.get()[0], 1.5, 1E-5);

    }

}