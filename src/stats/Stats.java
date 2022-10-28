package stats;

import main.InputOutput;
import util.EntryComparator;
import util.SortMethods;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Stats {
    protected static void makeSimpleTopTen(Set<Map.Entry<String, Integer>> entrySet, int n, InputOutput io) {
        makeTopN(entrySet, 10, "", "met", "berichten.", io);
    }

    protected static void makeTopN(Set<Map.Entry<String, Integer>> entrySet, int n, String firstText, String secondText, String thirdText, InputOutput io) {
        ArrayList<Map.Entry<String, Integer>> entryList = SortMethods.mergeSort(new ArrayList(entrySet), new EntryComparator());
        int i = 1;
        for (Map.Entry<String, Integer> entry : entryList) {
            io.output(i + ". " + firstText + " " + entry.getKey() + " " + secondText + " " + entry.getValue() + " " + thirdText);
            if (i >= n) {
                break;
            }
            i++;
        }
    }
}
