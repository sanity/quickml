package quickml.supervised.tree.nodes;

import com.google.common.collect.Maps;

import java.util.TreeMap;

/**
 * Created by alexanderhawk on 4/28/15.
 */
public class LeafDepthStats {
    public int ttlDepth = 0;
    public int ttlSamples = 0;
    public TreeMap<Integer, Long> depthDistribution = Maps.newTreeMap();
 }
