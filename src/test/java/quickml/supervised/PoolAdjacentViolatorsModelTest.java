package quickml.supervised.poolAdjacentViolatorsModelTest;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.junit.Test;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by alexanderhawk on 10/17/14.
 */
public class PoolAdjacentViolatorsModelTest {

  @Test
  public void createCalibrationSetTestUnweighted() {
      List<PoolAdjacentViolatorsModel.Observation> observationList = Lists.newArrayList();
      observationList.add(new PoolAdjacentViolatorsModel.Observation(1, 1));
      observationList.add(new PoolAdjacentViolatorsModel.Observation(2, 0.5));
      observationList.add(new PoolAdjacentViolatorsModel.Observation(3, 2));

      PoolAdjacentViolatorsModel poolAdjacentViolatorsModel = new PoolAdjacentViolatorsModel(observationList);
      TreeSet<PoolAdjacentViolatorsModel.Observation> calibrationSet = poolAdjacentViolatorsModel.createCalibrationSet(observationList);
      Assert.assertTrue(calibrationSet.first().input == 1.5);
      Assert.assertTrue(calibrationSet.first().output == 0.75);

      Assert.assertTrue(calibrationSet.last().input == 3);
      Assert.assertTrue(calibrationSet.last().output == 2);
  }

    @Test
    public void createCalibrationSetTestWeighted() {
        List<PoolAdjacentViolatorsModel.Observation> observationList = Lists.newArrayList();
        observationList.add(new PoolAdjacentViolatorsModel.Observation(1, 1, 3));
        observationList.add(new PoolAdjacentViolatorsModel.Observation(2, 0.5, 1));
        observationList.add(new PoolAdjacentViolatorsModel.Observation(3, 2, 1));

        PoolAdjacentViolatorsModel poolAdjacentViolatorsModel = new PoolAdjacentViolatorsModel(observationList);
        TreeSet<PoolAdjacentViolatorsModel.Observation> calibrationSet = poolAdjacentViolatorsModel.createCalibrationSet(observationList);
        Assert.assertTrue(calibrationSet.first().input == 1.25);
        Assert.assertTrue(calibrationSet.first().output == 0.875);
        Assert.assertTrue(calibrationSet.first().weight == 4);

        Assert.assertTrue(calibrationSet.last().input == 3);
        Assert.assertTrue(calibrationSet.last().output == 2);
    }
}
