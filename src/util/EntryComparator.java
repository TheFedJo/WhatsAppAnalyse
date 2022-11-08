package util;

import java.util.Comparator;
import java.util.Map;

public class EntryComparator implements Comparator<Map.Entry<Object, Number>> {
    @Override
    public int compare(Map.Entry<Object, Number> o1, Map.Entry<Object, Number> o2) {
        return (int) ((o1.getValue().floatValue() - o2.getValue().floatValue()) * 1000);
    }
}
