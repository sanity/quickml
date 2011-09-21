package com.moboscope.quickdt;


/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public interface Scorer {
	/**
	 * Assess the quality of a separation of data
	 * 
	 * @param a
	 *            A count of the number of classifications with a given
	 *            classification in split a
	 * @param b
	 *            A count of the number of classifications with a given
	 *            classification in split b
	 * @return A score, where a higher value indicates a better split. A value
	 *         of 0 being the lowest, and indicating no value.
	 */
	public double scoreSplit(ClassificationCounter a, ClassificationCounter b);
}