package stats;

import main.InputOutput;
import util.EntryComparator;
import util.Math;
import util.SortMethods;

import java.util.*;

public class Stats {
    public static void makeSimpleTopTen(Set<Map.Entry<String, Integer>> entrySet, InputOutput io) {
        makeTopIntegerN(entrySet, 10, "", "met", "berichten.", io);
    }

    /**
     * Make a topN with length n (length of the set if n = 0) in the following format:
     *
     * n. firstText author secondText number thirdText
     * @param entrySet
     * @param n
     * @param firstText
     * @param secondText
     * @param thirdText
     * @param io
     */
    public static void makeTopIntegerN(Set<Map.Entry<String, Integer>> entrySet, int n, String firstText, String secondText, String thirdText, InputOutput io) {
        ArrayList<Map.Entry<String, Integer>> entryList = SortMethods.mergeSort(new ArrayList(entrySet), new EntryComparator());
        int i = 1;
        for (Map.Entry<String, Integer> entry : entryList) {
            io.output(i + ". " + firstText + " " + entry.getKey() + " " + secondText + " " + entry.getValue() + " " + thirdText);
            if (i == n) {
                break;
            }
            i++;
        }
    }

    public static void makeTopDoubleN(Set<Map.Entry<String, Double>> entrySet, int n, String firstText, String secondText, String thirdText, InputOutput io) {
        ArrayList<Map.Entry<String, Double>> entryList = SortMethods.mergeSort(new ArrayList(entrySet), new EntryComparator());
        int i = 1;
        for (Map.Entry<String, Double> entry : entryList) {
            io.output(i + ". " + firstText + " " + entry.getKey() + " " + secondText + " " + Math.round(entry.getValue(), 2) + " " + thirdText);
            if (i == n) {
                break;
            }
            i++;
        }
    }
}
