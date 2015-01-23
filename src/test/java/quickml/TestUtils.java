package quickml;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasEntry;

public class TestUtils {

    public static <K, V> Matcher<Map<K, V>> matchesEntriesIn(Map<K, V> map) {
        return allOf(buildMatcherArray(map));
    }

    public static <K, V> Matcher<Map<K, V>> matchesAnyEntryIn(Map<K, V> map) {
        return anyOf(buildMatcherArray(map));
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Matcher<Map<? extends K, ? extends V>>[] buildMatcherArray(Map<K, V> map) {
        List<Matcher<Map<? extends K, ? extends V>>> entries = new ArrayList<Matcher<Map<? extends K, ? extends V>>>();
        for (K key : map.keySet()) {
            entries.add(hasEntry(key, map.get(key)));
        }
        return entries.toArray(new Matcher[entries.size()]);
    }

}
