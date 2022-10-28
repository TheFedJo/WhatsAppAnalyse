package main;

import parse.MessageType;
import parse.WhatsAppMessage;
import parse.WhatsAppMessageParser;
import stats.Charts;
import stats.WordStats;
import util.EntryComparator;
import util.SortMethods;
import util.Timer;

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

        File inputFile = new File(String.valueOf(Paths.get("data", "chats", "Intel25-08-22.txt")));
        System.out.println("Using file " + inputFile.getName() + " with size: " + inputFile.length() + " bytes.");
        InputOutput io = new InputOutput(inputFile);

        new WhatsAppMessageParser(inputFile, whatsAppMessages).parseFullFile();
        whatsAppMessages.removeIf(message -> message.getMessageType() != MessageType.STANDARD);

        timer.stopStart("Parsing");

        authorList = WordStats.createAuthorList(whatsAppMessages);
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

        new WordStats(io, whatsAppMessages).allStats(wordOccurrencePerAuthor);
        new Charts(io, whatsAppMessages).allCharts();

        timer.stop("Calculating other stats");
        System.out.println("Now enter your query");
    }
}

