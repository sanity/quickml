package quickml.supervised.tree.decisionTree.reducers;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.twitter.common.stats.ReservoirSampler;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.branchFinders.SplittingUtilsTest;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

    @Test
    public void fillReservoirSampler(){
        List<ClassifierInstance> instances = SplittingUtilsTest.getExtendedInstances();

         ReservoirSampler<Double> rs = DTNumBranchReducer.<ClassifierInstance>fillReservoirSampler(instances, "t", 4);
        Assert.assertEquals(Iterables.size(rs.getSamples()), 4);

        for (int i = 1; i< 20; i++ ) {
            instances.addAll(SplittingUtilsTest.getExtendedInstances());
        }

        Random rand = new Random();
        for (int i = 1; i< 1000; i++ ) {
            int one = rand.nextInt(instances.size());
            int two = rand.nextInt(instances.size());
            ClassifierInstance ci1 = instances.get(one);
            ClassifierInstance ci2 = instances.get(two);

            instances.set(one, ci2);
            instances.set(two, ci1);
        }

        rs = DTNumBranchReducer.<ClassifierInstance>fillReservoirSampler(instances, "t", 4);
        Assert.assertEquals(Iterables.size(rs.getSamples()), 4);
    }

    @Test
    public void getAttributeStatsOptionalTest(){
        //fix
        List<ClassifierInstance> instances = SplittingUtilsTest.getExtendedInstances();
        double []splits ={2.5, 4.5, 6.5};
        Optional<AttributeStats<ClassificationCounter>> attributeStatsOptional = DTNumBranchReducer.<ClassifierInstance>getAttributeStatsOptional("t", splits, instances);
        Assert.assertTrue(attributeStatsOptional.isPresent());
        AttributeStats<ClassificationCounter> attributeStats = attributeStatsOptional.get();
        Assert.assertEquals(attributeStats.getStatsOnEachValue().size(), 4);
        ClassificationCounter cc = attributeStats.getStatsOnEachValue().get(0);
        Assert.assertEquals(cc.getTotal(), 2.0, 1E-5);
        Assert.assertTrue(cc.allClassifications().contains(0.0) && !cc.allClassifications().contains(1.0));

        splits =new double[1];
        splits[0] = 1.5;
        attributeStatsOptional = DTNumBranchReducer.<ClassifierInstance>getAttributeStatsOptional("t", splits, instances);
        cc = attributeStatsOptional.get().getStatsOnEachValue().get(1);
        Assert.assertTrue(cc.allClassifications().contains(0.0) && cc.allClassifications().contains(1.0));
        Assert.assertEquals(cc.getCount(1.0), 6.0, 1E-5);
        Assert.assertEquals(cc.getCount(0.0), 1.0, 1E-5);






    }

}