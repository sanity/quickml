package com.moboscope.quickdt.scorers;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import com.moboscope.quickdt.TreeBuilder.Scorer;

public class GiniImpurityScorer implements Scorer {

	public double scoreSplit(final int aTtl, final Map<Serializable, Integer> a, final int bTtl,
			final Map<Serializable, Integer> b) {
		final Entry<Serializable, Integer> mca = mostCommon(a);
		final Entry<Serializable, Integer> mcb = mostCommon(b);

		if (aTtl == 0 || bTtl == 0)
			return 0;

		return ((double) mca.getValue() + (double) mcb.getValue()) / (aTtl + bTtl);
	}

	public Entry<Serializable, Integer> mostCommon(final Map<Serializable, Integer> counts) {
		Entry<Serializable, Integer> mc = null;
		for (final Entry<Serializable, Integer> e : counts.entrySet()) {
			if (mc == null || e.getValue() > mc.getValue()) {
				mc = e;
			}
		}
		return mc;
	}

}
