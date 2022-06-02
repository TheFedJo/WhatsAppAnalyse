import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static ArrayList<WhatsAppMessage> whatsAppMessages = new ArrayList<>();
    public static ArrayList<String> authorList;
    public static ArrayList<Map.Entry<String, Integer>> sortedAuthorMessageCountEntries;
    public static Map<String, Integer> messageCountPerAuthor;
    public static Map<String, Map<String, Integer>> wordOccurrencePerAuthor;
    public static ArrayList<Map.Entry<String, Double>> informationPerAuthor;
    private static final Timer timer = new Timer();

    public static void main(String[] args) {
        timer.start();

        File inputFile = new File(String.valueOf(Paths.get("data", "chats", "DGM28-04-22.txt")));
        System.out.println("Using file " + inputFile.getName() + " with size: " + inputFile.length() + " bytes.");
        InputOutput io = new InputOutput(inputFile);

        new WhatsAppMessageParser(inputFile, whatsAppMessages).parseFullFile();
        filterToStandardMessagesOnly(whatsAppMessages);

        timer.stopStart("Parsing");

        WordStats.countMessages(whatsAppMessages, io);

        timer.stopStart("Counting");

        wordOccurrencePerAuthor = WordStats.wordOccurrenceMapPerAuthor(whatsAppMessages, authorList);
        informationPerAuthor = SortMethods.mergeSort(new ArrayList<>(WordStats.informationPerAuthor(wordOccurrencePerAuthor).entrySet()), new EntryComparator());
        int i = 1;
        io.output("\n");
        for (Map.Entry<String, Double> entry : informationPerAuthor) {
            io.output(i + ". " + entry.getKey() + " met " + WordStats.round(entry.getValue(), 2) + " bits verschillende informatie.");
            i++;
        }

        new WordStats(io).allStats(wordOccurrencePerAuthor);
        new Charts(io, whatsAppMessages).allCharts();

        timer.stop("Calculating other stats");
        System.out.println("Now enter your query");
    }

    static void filterToStandardMessagesOnly(ArrayList<WhatsAppMessage> messages) {
        messages.removeIf(message -> message.getMessageType() != MessageType.STANDARD);
    }

}

class EntryComparator implements Comparator<Map.Entry<String, Number>>{
    @Override
    public int compare(Map.Entry<String, Number> o1, Map.Entry<String, Number> o2) {
        return (int) ((o1.getValue().floatValue() - o2.getValue().floatValue()) * 1000);
    }
}

class SortMethods {
     static ArrayList mergeSort(ArrayList elements, Comparator comparator) {
        if (elements.size() <= 1) {
            return elements;
        }
        ArrayList firstList = mergeSort(new ArrayList(elements.subList(0, elements.size() / 2)), comparator);
        ArrayList secondList = mergeSort(new ArrayList(elements.subList(elements.size() / 2, elements.size())), comparator);
        int fi = 0;
        int si = 0;
        ArrayList result = new ArrayList();
        while (fi < firstList.size() && si < secondList.size()) {
            if(comparator.compare(firstList.get(fi), secondList.get(si)) > 0) {
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

class Timer {
    private long startTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop(String activity) {
        long endTime = System.currentTimeMillis();
        System.out.println(activity + " took " + (endTime - startTime) + " ms.");
    }

    public void stopStart(String activity) {
        stop(activity);
        start();
    }
}