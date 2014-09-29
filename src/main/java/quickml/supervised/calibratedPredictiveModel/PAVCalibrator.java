package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class PAVCalibrator implements Serializable, Calibrator {
    private static final Logger logger = LoggerFactory.getLogger(PAVCalibrator.class);

    private static final long serialVersionUID = 4389814244047503245L;
    private int size;
    TreeSet<Observation> calibrationSet = Sets.newTreeSet();

    private static Random rand = new Random();

    public PAVCalibrator(final Iterable<Observation> predictions) {
        this(predictions, 1);
    }

    /**
     * @param predictions The input to the calibration function
     * @param minWeight   The minimum weight of a point, used to pre-smooth the function
     */
    public PAVCalibrator(final Iterable<Observation> predictions, int minWeight) {
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
                calibrationSet.add(toAdd);
                toAdd = p;
            }
            if (toAdd != null)
                calibrationSet.add(toAdd);
        } else {
            calibrationSet.addAll(orderedCalibrations);
        }

        final Observation restartPos = null;
        cont:
        while (true) {
            Observation cur = null, last = null;
            for (final Observation n : restartPos == null ? calibrationSet : calibrationSet.tailSet(restartPos, true)) {
                last = cur;
                cur = n;
                if (cur != null && last != null && (cur.output <= last.output)) {
                    calibrationSet.remove(cur);
                    calibrationSet.remove(last);
                    Observation merged = last.mergeWith(cur);
                    calibrationSet.add(merged);
                    continue cont;
                }
            }
            break;
        }

        this.size = calibrationSet.size();
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


    public double correct(final double input) {
        final double kProp;
        final Observation toCorrect = new Observation(input, 0);
        Observation floor = calibrationSet.floor(toCorrect);
        if (floor == null) {
            floor = new Observation(0, 0);
        }
        Observation ceiling = calibrationSet.ceiling(toCorrect);
        if (ceiling == null) {
            try{
                return Math.max(input, calibrationSet.last().output);
            }
            catch (NoSuchElementException e){
                System.out.println("break point me");
            }

        }

        boolean inputOnAPointInTheCalibrationSet = ceiling.input == input || input == floor.input;
        boolean ceilingInputEqualFloorInput = ceiling.input == floor.input;
        boolean exceptionalCase = ceilingInputEqualFloorInput || inputOnAPointInTheCalibrationSet;
        if (exceptionalCase)
            return input == ceiling.input ? ceiling.output : input;

        kProp = (input - floor.input) / (ceiling.input - floor.input);
        double corrected = floor.output + ((ceiling.output - floor.output) * kProp);
        if (Double.isInfinite(corrected) || Double.isNaN(corrected)) {
            return input;
        } else {
            return corrected;
        }
    }

    public double reverse(final double output) {
        double lowCPC = calibrationSet.first().input, highCPC = calibrationSet.last().input;
        for (int x = 0; x < 16; x++) {
            final double tst = (lowCPC + highCPC) / 2.0;
            final double opt = correct(tst);
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
        for (final PAVCalibrator.Observation observation : calibrationSet) {
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
         * This type of observation can be used to correct a previous observation.
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
            seed = PAVCalibrator.rand.nextInt();
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
            if (weight == 0 && other.weight == 0) {
                return Observation.newWeightless((input + other.input) / 2.0, (output + other.output) / 2.0);
            } else if (other.weight == 0) {
                return other.mergeWith(this);
            } else if (weight == 0) {
                return new Observation(other.input,
                        (this.output + other.output * other.weight) / (other.weight + 1),
                        other.weight);
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
