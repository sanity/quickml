package quickml.supervised.regressionModel.IsotonicRegression;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.regressionModel.SingleVariableRealValuedFunction;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

// TODO: This should be split out into a separate Builder rather than using the constructor,
//       so that it follows the same pattern as other PredictiveModels
public class PoolAdjacentViolatorsModel implements SingleVariableRealValuedFunction  {
    private static final Logger logger = LoggerFactory.getLogger(PoolAdjacentViolatorsModel.class);

    private static final long serialVersionUID = 4389814244047503245L;
    private int size;
    ArrayList<Observation> calibrationList = Lists.newArrayList();
    TreeSet<Observation> calibrationSet = Sets.newTreeSet();
    TreeSet<Observation> preSmoothingSet = Sets.newTreeSet();

    boolean reversed = false;

    private static Random rand = new Random();
    private boolean interpolateThroughOrigin = true;
    private boolean extropolateOffUpperEnd = true;



    public PoolAdjacentViolatorsModel(final Iterable<Observation> predictions) {
        this(predictions, 1);
    }

    /**
     * @param predictions The input to the calibration function
     * @param minWeight   The minimum weight of a point, used to pre-smooth the function
     */


    public PoolAdjacentViolatorsModel(final Iterable<Observation> predictions, int minWeight) {
        Preconditions.checkNotNull(predictions);
        Preconditions.checkArgument(minWeight >= 1, "minWeight %s must be >= 1", minWeight);

        TreeSet<Observation> orderedCalibrations = Sets.newTreeSet();

        Iterables.addAll(orderedCalibrations, predictions);

        if (minWeight > 1) {
            Observation toAdd = null;
            for (final Observation p : orderedCalibrations) {
                if (toAdd == null) {
                    toAdd = p;
                    continue;
                }
                if (toAdd.weight < minWeight) {
                    toAdd = toAdd.mergeWith(p);
                    continue;
                }
                calibrationList.add(toAdd);
                toAdd = p;
            }
            if (toAdd != null)
                calibrationList.add(toAdd);
        } else {
            calibrationList.addAll(orderedCalibrations);
        }
        preSmoothingSet.addAll(calibrationList);
        calibrationSet = createCalibrationSet(calibrationList);
        this.size = calibrationSet.size();

    }

    public TreeSet<Observation> createCalibrationSet(List<Observation> inputOrderedList) {
        Observation currentObservation = null, preceedingObservation = null;
        int preceedingObservationIndex = 0;

        for (int i = 1; i<inputOrderedList.size(); i++) {
            boolean currentObservationIsViolator = true;
            currentObservation = inputOrderedList.get(i);

            while (currentObservationIsViolator) {

                if (preceedingObservationIndex >= 0) {
                    preceedingObservation = inputOrderedList.get(preceedingObservationIndex);
                } else {
                    break;
                }

                if (!reversed) {
                    currentObservationIsViolator = currentObservation.output < preceedingObservation.output;
                } else {
                    currentObservationIsViolator = currentObservation.output > preceedingObservation.output;
                }

                if (currentObservationIsViolator) {
                    currentObservation = preceedingObservation.mergeWith(currentObservation);
                    preceedingObservationIndex--;
                }
            }
            inputOrderedList.set(preceedingObservationIndex+1, currentObservation);
            preceedingObservationIndex++;
        }
        TreeSet<Observation> localCalibrationSet = Sets.newTreeSet();
        for (int i = 0; i< preceedingObservationIndex+1; i++) {
            localCalibrationSet.add(inputOrderedList.get(i));
        }
        return localCalibrationSet;
    }

    public PoolAdjacentViolatorsModel interpolateThroughOrigin(boolean interpolateThroughOrigin) {
        this.interpolateThroughOrigin = interpolateThroughOrigin;
        return this;
    }

    public PoolAdjacentViolatorsModel extropolateOffUpperEnd(boolean extropolateOffUpperEnd) {
        this.extropolateOffUpperEnd = extropolateOffUpperEnd;
        return this;
    }


    public TreeSet<Observation> getCalibrationSet(){
        return calibrationSet;
    }
    public TreeSet<Observation> getPreSmoothingSet(){
        return preSmoothingSet;
    }

    public void stripZeroOutputs() {
        while (!calibrationSet.isEmpty() && calibrationSet.first().output == 0) {
            calibrationSet.pollFirst();
        }

        this.size = calibrationSet.size();
    }

    public void addObservation(Observation observation) {
        calibrationSet.add(observation);
    }

    public boolean willExtrapolate(double input) {
        final Observation toCorrect = new Observation(input, 0);
        Observation floor = calibrationSet.floor(toCorrect);
        Observation ceil = calibrationSet.ceiling(toCorrect);
        if (floor == null || ceil ==null)
            return true;
        else
            return false;
    }

    @Override
    public Double predict(Double input) {
        Preconditions.checkState(!calibrationSet.isEmpty());
        final double kProp;
        final Observation toCorrect = new Observation(input, 0);
        Observation floor = calibrationSet.floor(toCorrect);
        if (!interpolateThroughOrigin && floor == null && calibrationSet.higher(calibrationSet.first()) != null) {
            double upperXCoord = 0, upperYCoord = 0;
            if (calibrationSet.higher(calibrationSet.first()) != null) {
                upperXCoord = (calibrationSet.higher(calibrationSet.first())).input;
                upperYCoord = (calibrationSet.higher(calibrationSet.first())).output;
            }
            try {
                double slopeOffEnd = (upperYCoord - calibrationSet.first().output) /
                        (upperXCoord - calibrationSet.first().input);
                double inputDistanceFromFirst = input - calibrationSet.first().input;
                return Math.max(0, calibrationSet.first().output + slopeOffEnd * inputDistanceFromFirst);
            } catch (NoSuchElementException e) {
                logger.warn("NoSuchElementException finding calibrationSet elements");
                return calibrationSet.first().output;
            }

        } else if (floor ==null) {
            floor = new Observation(0, 0, calibrationSet.first().weight);
        }

        Observation ceiling = calibrationSet.ceiling(toCorrect);
        if (ceiling == null && extropolateOffUpperEnd) {
            double lowerXcoord = 0, lowerYCoord = 0;
            if (calibrationSet.lower(calibrationSet.last()) != null) {
                lowerXcoord = calibrationSet.lower(calibrationSet.last()).input;
                lowerYCoord = calibrationSet.lower(calibrationSet.last()).output;
            } else {
                return floor.output;
            }
            try {
                double slopeOffEnd = (calibrationSet.last().output - lowerYCoord) /
                        (calibrationSet.last().input - lowerXcoord);
                double inputDistanceFromLast = input - calibrationSet.last().input;
                return calibrationSet.last().output + slopeOffEnd * inputDistanceFromLast;
            } catch (NoSuchElementException e) {
                logger.warn("NoSuchElementException finding ceiling or calibrationSet has no element calibrationSet.lower(calibrationSet.last()).input");
                return floor.output;
            }
        }

        boolean inputOnAPointInTheCalibrationSet = input.equals(ceiling.input) || input.equals(floor.input);
        if (inputOnAPointInTheCalibrationSet) {
            return input.equals(ceiling.input) ? ceiling.output : floor.output;
        }
        //PAV has just one point in calibration set
        boolean ceilingInputEqualFloorInput = ceiling.input == floor.input;
        if (ceilingInputEqualFloorInput)
            return input.equals(ceiling.input) ? ceiling.output : floor.output;

        double floorWeight = (ceiling.input - input)*floor.weight;
        double ceilingWeight = (input - floor.input)*ceiling.weight;
        double corrected = (floor.output*floorWeight + ceiling.output*ceilingWeight)/(floorWeight + ceilingWeight);


     // kProp = (input - floor.input) / (ceiling.input - floor.input);
     //   double corrected = floor.output + ((ceiling.output - floor.output) * kProp);
        if (Double.isInfinite(corrected) || Double.isNaN(corrected)) {
            logger.info("corrected is NaN or inf");
            return input;
        } else {
            return corrected;
        }
    }

    public double reverse(final double output) {
        double lowCPC = calibrationSet.first().input, highCPC = calibrationSet.last().input;
        for (int x = 0; x < 16; x++) {
            final double tst = (lowCPC + highCPC) / 2.0;
            final double opt = predict(tst);
            if (opt < output) {
                lowCPC = tst;
            } else {
                highCPC = tst;
            }
        }
        return (lowCPC + highCPC) / 2.0;
    }

    public void dump(final Appendable ps) {
        for (final Observation p : calibrationSet) {
            try {
                ps.append(p + "\n");
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        dump(sb);
        return sb.toString();
    }

    public int size() {
        return size;
    }

    public Observation minNonZeroObservation() {
        Observation minObs = null;
        for (final PoolAdjacentViolatorsModel.Observation observation : calibrationSet) {
            if (observation.input >= 0.0) {
                minObs = observation;
                break;
            }
        }
        return minObs;
    }

    public Observation maxObservation() {
        return calibrationSet.last();
    }

    public static final class Observation implements Comparable<Observation>, Serializable {
        private static final long serialVersionUID = -5472613396250257288L;
        public final double input;
        public final double output;
        private final int seed;
        public final double weight;

        /**
         * This type of observation can be used to predict a previous observation.
         * So adding:
         * Observation(1, 0) and Observation.WEIGHTLESS(1, 2)
         *
         * Has the exact same effect as adding:
         * Observation(1, 1)
         *
         * @param input
         * @param output
         * @return
         */
        public static Observation newWeightless(final double input, final double output) {
            return new Observation(input, output, 0);
        }

        public Observation(final double input, final double output) {
            this(input, output, 1);
        }

        public Observation(final double input, final double output, final double weight) {
            Preconditions.checkState(!(Double.isNaN(input) && Double.isNaN(output) && Double.isNaN((double) weight)));
            this.input = input;
            this.output = output;
            seed = PoolAdjacentViolatorsModel.rand.nextInt();
            this.weight = weight;
        }

        @Override
        public int compareTo(final Observation o) {
            final int r = Double.compare(input, o.input);
            if (r != 0)
                return r;
            return Double.compare(seed, o.seed);
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof Observation)
                return ((Observation) o).seed == seed;
            else
                return false;
        }


        public Observation mergeWith(final Observation other) {
            if ((weight == 0 && other.weight == 0) || (weight + other.weight == 0))  {
                return Observation.newWeightless((input + other.input) / 2.0, (output + other.output) / 2.0);
            } else if (other.weight == 0) {
                return this;//other.mergeWith(this);
            } else if (weight == 0) {
                return other;//new Observation(other.input,
                // (this.output + other.output * other.weight) / (other.weight + 1),
                //        other.weight);
            }

            return new Observation(
                    (input * weight + other.input * other.weight) / (weight + other.weight),
                    (output * weight + other.output * other.weight) / (weight + other.weight),
                    weight + other.weight);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Observation [input=");
            builder.append(input);
            builder.append(", output=");
            builder.append(output);
            builder.append(", weight=");
            builder.append(weight);
            builder.append("]");
            return builder.toString();
        }
    }
}
