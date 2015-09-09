package quickml.supervised.crossValidation.data;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
//TODO: generalize this to make object a generic
public class OutOfTimeData<I> implements TrainingDataCycler<I> {
    private static final int MIN_INSTANCES_PER_VALIDATION_PERIOD = 5;
    private final List<I> allData;
    private final double crossValidationFraction;
    private final int timeSliceHours;
    private DateTimeExtractor<I> dateTimeExtractor;
    private List<I> trainingSet;
    private List<I> validationSet;
    private int validationPeriods;
    private static Logger logger = LoggerFactory.getLogger(OutOfTimeData.class);
    private DateTime endValidationPeriod;

    public OutOfTimeData(List<I> allData, double crossValidationFraction, int timeSliceHours, DateTimeExtractor dateTimeExtractor) {
        this.allData = allData;
        this.crossValidationFraction = crossValidationFraction;
        this.timeSliceHours = timeSliceHours;
        this.dateTimeExtractor = dateTimeExtractor;
        sortData();
        reset();
    }


    @Override
    public void reset() {
        endValidationPeriod = null;
        setTrainingSetBasedOnFraction();
        updateValidationSet();
    }

    @Override
    public List<I> getTrainingSet() {
        return trainingSet;
    }

    @Override
    public List<I> getValidationSet() {
        return validationSet;
    }

    @Override
    public List<I> getAllData() {
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
        List<I> potentialValidationSet = allData.subList(trainingSet.size(), allData.size());
        if (endValidationPeriod==null) {
             endValidationPeriod = dateTimeExtractor.extractDateTime(potentialValidationSet.get(0)).plusHours(timeSliceHours);
        } else {
            DateTime lastValidationPeriodEnd = endValidationPeriod;
            endValidationPeriod = lastValidationPeriodEnd.plusHours(timeSliceHours);
            logger.info("endValidationPeriod: {}", endValidationPeriod.toString());
        }
        validationSet = newArrayList();
        for (I instance : potentialValidationSet) {
            if (dateTimeExtractor.extractDateTime(instance).isBefore(endValidationPeriod))
                validationSet.add(instance);
            else if (validationSet.isEmpty()) {
                // If the set is empty and we are at the end of the validation period
                // so we increase the validation period
                endValidationPeriod = endValidationPeriod.plusHours(timeSliceHours);
                logger.info("no data in time window, pushing endValidationPeriod: {}", endValidationPeriod.toString());

            }
            else {
                break; //because the list is sorted, once the first if fails, and else if fails, the loop should end.
            }
        }
        logger.info("num instances in validation period: {}, with last entry at {}", validationSet.size(), dateTimeExtractor.extractDateTime(validationSet.get(validationSet.size()-1)));
    }


    private void sortData() {
        Collections.sort(allData, new Comparator<I>() {
            @Override
            public int compare(I o1, I o2) {
                return dateTimeExtractor.extractDateTime(o1).compareTo(dateTimeExtractor.extractDateTime(o2));
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
