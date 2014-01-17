package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.HashMapAttributes;
import quickdt.Instance;
import quickdt.TreeBuilder;
import quickdt.randomForest.RandomForest;
import quickdt.randomForest.RandomForestBuilder;

import java.util.List;
import java.util.Random;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class genExp {
    public static void main(String[] args) {
        ProbTest x = new ProbTest(100, 5, 20, 10, 10, .01, 2);
        x.getAverageDeviationInPredictedProbabilities(100, 0.002, true);
        //get prob for each .
    }

}
