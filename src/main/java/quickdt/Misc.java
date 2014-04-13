package quickdt;

import com.google.common.base.Optional;

import java.util.Map;
import java.util.Random;

public class Misc {
	public static final Random random = new Random();

    public static final <K, V extends Comparable<V>> Optional<Map.Entry<K, V>> getEntryWithLowestValue(Map<K, V> map) {
        Optional<Map.Entry<K, V>> entryWithLowestValue = Optional.absent();
        for (Map.Entry<K, V> kvEntry : map.entrySet()) {
            if (!entryWithLowestValue.isPresent() || entryWithLowestValue.get().getValue().compareTo(kvEntry.getValue()) >= 0){
                entryWithLowestValue = Optional.of(kvEntry);
            }
        }
        return entryWithLowestValue;
    }
}
