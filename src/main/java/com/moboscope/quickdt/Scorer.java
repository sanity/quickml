package com.moboscope.quickdt;

import java.io.Serializable;
import java.util.Map;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public interface Scorer {
	/**
	 * Assess the quality of a separation of data
	 * 
	 * @param aTtl
	 *            The total number of classifications in split a
	 * @param a
	 *            A map containing a count of the number of classifications with
	 *            a given classification in split a
	 * @param bTtlThe
	 *            total number of classifications in split b
	 * @param b
	 *            A map containing a count of the number of classifications with
	 *            a given classification in split b
	 * @return A score, where a higher value indicates a better split. A value
	 *         of 0 being the lowest, and indicating no value.
	 */
	public double scoreSplit(final int aTtl, final Map<Serializable, Integer> a, final int bTtl,
			final Map<Serializable, Integer> b);
}