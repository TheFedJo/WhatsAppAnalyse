package main;

import parse.MessageType;
import parse.WhatsAppChat;
import parse.WhatsAppMessage;
import parse.WhatsAppMessageParser;
import stats.Charts;
import stats.WordStats;
import util.EntryComparator;
import util.SortMethods;
import util.Timer;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static ArrayList<String> authorList;
    public static ArrayList<Map.Entry<String, Integer>> sortedAuthorMessageCountEntries;
    public static Map<String, Integer> messageCountPerAuthor;
    public static Map<String, Map<String, Integer>> wordOccurrencePerAuthor;
    public static ArrayList<Map.Entry<String, Double>> informationPerAuthor;
    private static final Timer timer = new Timer();
    public static WhatsAppChat chat;

    public static void main(String[] args) {
        timer.start();

        File inputFile = new File(String.valueOf(Paths.get("data", "chats", "Omzicht28-10-22.txt")));
        chat = new WhatsAppChat(inputFile.getName());
        System.out.println("Using file " + inputFile.getName() + " with size: " + inputFile.length() + " bytes.");
        InputOutput io = new InputOutput(inputFile);

        new WhatsAppMessageParser(inputFile, chat).parseFullFile();

        timer.stopStart("Parsing");

        WordStats.countMessages(chat, io);

        timer.stopStart("Counting");

        wordOccurrencePerAuthor = WordStats.wordOccurrenceMapPerAuthor(chat);
        informationPerAuthor = SortMethods.mergeSort(new ArrayList<>(WordStats.informationPerAuthor(wordOccurrencePerAuthor).entrySet()), new EntryComparator());
        int i = 1;
        io.output("\n");
        for (Map.Entry<String, Double> entry : informationPerAuthor) {
            io.output(i + ". " + entry.getKey() + " met " + WordStats.round(entry.getValue(), 2) + " bits verschillende informatie.");
            i++;
        }

        new WordStats(io, chat.getRegularMessages()).allStats(wordOccurrencePerAuthor);
        new Charts(io, chat.getRegularMessages()).allCharts();

        HashMap<String, Integer> deletedMessagesPerAuthor = new HashMap<>();
        for(String author : chat.getAuthorList()) {
            deletedMessagesPerAuthor.put(author, 0);
        }
        for (WhatsAppMessage message : chat.getDeletedMessages()) {
            deletedMessagesPerAuthor.replace(message.getAuthor(), deletedMessagesPerAuthor.get(message.getAuthor()) + 1);
        }
        ArrayList<Map.Entry<String, Integer>> adtLijst = SortMethods.mergeSort(new ArrayList<>(deletedMessagesPerAuthor.entrySet()), new EntryComparator());
        i = 1;
        io.output("\n");
        for (Map.Entry<String, Integer> entry : adtLijst) {
            io.output(i + ". " + entry.getKey() + " met " + entry.getValue() + " verwijderde berichten.");
            i++;
        }
        io.output("Admins hebben zoveel berichten verwijderd: " + chat.getAdminDeletedMessages().size());

        timer.stop("Calculating other stats");
        System.out.println("Now enter your query");
        HashMap<MessageType, Integer> map = new HashMap<MessageType, Integer>();
        for (MessageType type : MessageType.values()) {
            map.put(type, 0);
        }
        for (WhatsAppMessage message : chat.getMessages()) {
            if(message.getMessageType() != MessageType.STANDARD) {
                io.output(String.valueOf(message.getMessageType()));
                io.output(message.toString());
            }
            map.replace(message.getMessageType(), map.get(message.getMessageType()) + 1);
        }
        for (Map.Entry entry : map.entrySet()) {
            io.output(String.valueOf(entry));
        }




    }
}

