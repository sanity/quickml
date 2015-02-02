package quickml.supervised.alternative.optimizer;

import org.joda.time.DateTime;
import quickml.data.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class OutOfTimeData<T extends Instance> implements TrainingDataCycler<T> {

    private final List<T> allData;
    private final double crossValidationFraction;
    private final int timeSliceHours;
    private List<T> trainingSet;
    private List<T> validationSet;

    public OutOfTimeData(List<T> allData, double crossValidationFraction, int timeSliceHours) {
        this.allData = allData;
        this.crossValidationFraction = crossValidationFraction;
        this.timeSliceHours = timeSliceHours;
        sortData();
        reset();
    }

    @Override
    public void reset() {
        setTrainingSetBasedOnFraction();
        updateValidationSet();
    }

    @Override
    public List<T> getTrainingSet() {
        return trainingSet;
    }

    @Override
    public List<T> getValidationSet() {
        return validationSet;
    }

    @Override
    public List<T> getAllData() {
        return allData;
    }

    @Override
    public void nextCycle() {
        if (hasMore()) {
            trainingSet.addAll(validationSet);
            updateValidationSet();
        }
    }

    @Override
    public boolean hasMore() {
        return trainingSet.size() + validationSet.size() < allData.size();
    }


    private void updateValidationSet() {
        List<T> potentialValidationSet = allData.subList(trainingSet.size(), allData.size());
        DateTime endValidationPeriod = potentialValidationSet.get(0).getTimestamp().plusHours(timeSliceHours);

        validationSet = newArrayList();
        for (T instance : potentialValidationSet) {
            if (instance.getTimestamp().isBefore(endValidationPeriod))
                validationSet.add(instance);
            else if (validationSet.isEmpty()) {
                // If the set is empty and we are at the end of the validation period
                // so we increase the validation period
                endValidationPeriod = endValidationPeriod.plusHours(timeSliceHours);
            }
        }
    }

    private void sortData() {
        Collections.sort(allData, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
    }

    private void setTrainingSetBasedOnFraction() {
        int size = (int) (allData.size() * (1 - crossValidationFraction));
        verifySizeIsLessThanTotalSize(allData, size);
        trainingSet = new ArrayList<>(allData.subList(0, size));
    }

    private static void verifySizeIsLessThanTotalSize(List data, int size) {
        if (size == data.size()) {
            throw new RuntimeException("fractionOfDataForCrossValidation must be non zero");
        }
    }




}
