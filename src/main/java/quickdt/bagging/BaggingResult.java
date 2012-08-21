package quickdt.bagging;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.javatuples.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/**
 * <p>
 * Classification result of a {@link BaggedTree}.
 * </p>
 * 
 * @author Philipp Katz
 */
public class BaggingResult {

    private final Multiset<Serializable> results;

    BaggingResult(Multiset<Serializable> results) {
	this.results = results;
    }

    /**
     * <p>
     * Get the winner class of the bagging, i.e. the class with the must votes.
     * The returned {@link Pair} contains the winner class and the percentage of
     * votes for this class (which can be considered as confidence for the
     * classification).
     * </p>
     * 
     * @return {@link Pair} with classification and probability (0,1].
     */
    public Pair<Serializable, Double> getClassification() {
	Serializable winner = null;
	int winnerCount = 0;
	for (Serializable result : results.elementSet()) {
	    int count = results.count(result);
	    if (count > winnerCount) {
		winnerCount = count;
		winner = result;
	    }
	}
	if (winner == null) {
	    return null;
	}
	double confidence = (double) winnerCount / results.size();
	return new Pair<Serializable, Double>(winner, confidence);
    }

    /**
     * <p>
     * Return a sorted list of all assigned classes by the bagging, ordered
     * descendingly, i.e. the class with the highest probability first. Classes
     * which have a probability of zero are not included in the result.
     * </p>
     * 
     * @return {@link List} of {@link Pair}s with classification and probability (0,1].
     */
    public List<Pair<Serializable, Double>> getAllClassifications() {

	List<Entry<Serializable>> entryList = Lists.newArrayList(results
		.entrySet());
	Collections.sort(entryList, new FrequencyComparator());

	List<Pair<Serializable, Double>> ret = Lists.newArrayList();
	for (Entry<Serializable> entry : entryList) {
	    double confidence = (double) results.count(entry.getElement())
		    / results.size();
	    ret.add(new Pair<Serializable, Double>(entry.getElement(),
		    confidence));
	}

	return ret;
    }

    @Override
    public String toString() {
	StringBuilder buildToString = new StringBuilder();
	List<Pair<Serializable, Double>> classifications = getAllClassifications();
	for (Pair<Serializable, Double> classification : classifications) {
	    if (buildToString.length() > 0) {
		buildToString.append(", ");
	    }
	    buildToString.append(classification);
	}
	return buildToString.toString();
    }

    private static final class FrequencyComparator implements
	    Comparator<Multiset.Entry<?>> {
	@Override
	public int compare(Entry<?> o1, Entry<?> o2) {
	    return Integer.valueOf(o2.getCount()).compareTo(o1.getCount());
	}

    }

}
