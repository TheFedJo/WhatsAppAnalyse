package util;

import java.util.ArrayList;
import java.util.Comparator;

public class SortMethods {
    public static ArrayList mergeSort(ArrayList elements, Comparator comparator) {
        if (elements.size() <= 1) {
            return elements;
        }
        ArrayList firstList = mergeSort(new ArrayList(elements.subList(0, elements.size() / 2)), comparator);
        ArrayList secondList = mergeSort(new ArrayList(elements.subList(elements.size() / 2, elements.size())), comparator);
        int fi = 0;
        int si = 0;
        ArrayList result = new ArrayList();
        while (fi < firstList.size() && si < secondList.size()) {
            if (comparator.compare(firstList.get(fi), secondList.get(si)) > 0) {
                result.add(firstList.get(fi));
                fi++;
            } else {
                result.add(secondList.get(si));
                si++;
            }
        }
        if (fi == firstList.size() && si != secondList.size()) {
            result.addAll(secondList.subList(si, secondList.size()));
        } else {
            result.addAll(firstList.subList(fi, firstList.size()));
        }
        return result;
    }
}
