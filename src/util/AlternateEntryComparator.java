package util;

import java.util.Comparator;
import java.util.Map;

public class AlternateEntryComparator implements Comparator<Map.Entry<Long, Number>> {
    @Override
    public int compare(Map.Entry<Long, Number> o1, Map.Entry<Long, Number> o2) {
        return (int) (o2.getKey() - o1.getKey());
    }
}
