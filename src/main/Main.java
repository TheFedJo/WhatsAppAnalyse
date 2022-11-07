package main;

import parse.WhatsAppChat;
import parse.WhatsAppMessageParser;
import stats.Charts;
import stats.Stats;
import stats.WordStats;
import util.Timer;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static Map<String, Map<String, Integer>> wordOccurrencePerAuthor;
    private static final Timer timer = new Timer();
    public static WhatsAppChat chat;
    private static final File dataFolder = new File(String.valueOf(Paths.get("data", "chats")));
    private static InputOutput io;

    public static void main(String[] args) {
        ArrayList<File> fileList = new ArrayList<>(List.of(Objects.requireNonNull(dataFolder.listFiles())));
        fileList.removeIf(File::isDirectory);
        fileList.removeIf(file -> !file.getName().endsWith(".txt"));
        System.err.printf("Files found in %s:\n", fileList.get(0).getParentFile().getAbsolutePath());
        fileList.stream().map(File::getName).forEach(System.err::println);
        boolean parseAllChats = false;
        boolean parseSeveralChats = true;
        if (parseAllChats) {
            for (File file : fileList) {
                parseFile(file);
            }
        } else if(parseSeveralChats) {
            File[] fileArray = new File[2];
            fileArray[0] = new File(dataFolder, "Intel25-08-22.txt");
            fileArray[1] = new File(dataFolder, "Intel25-10-22.txt");
            parseFiles(fileArray);
        } else {
            parseFile(new File(dataFolder, "chat.txt"));
        }
        System.out.println("Now enter your query");
    }

    private static void parseFile(File chatFile) {
        timer.start();
        chat = new WhatsAppChat(chatFile.getName());
        System.out.println("Using file " + chatFile.getName() + " with size: " + chatFile.length() + " bytes.");
        io = new InputOutput(chatFile);
        new WhatsAppMessageParser(chatFile, chat).parseFullFile();
        io.output("This chat's earliest message is from: " + chat.getRegularMessages().get(0).getDateTime().toString());
        analyzeChat();
    }

    private static void parseFiles(File[] chatFiles) {
        timer.start();
        chat = new WhatsAppChat(chatFiles[0].getName());

        StringBuilder sb = new StringBuilder();
        for (File file : chatFiles) {
            String fileName = file.getName().split("\\.")[0];
            sb.append(fileName);
        }
        io = new InputOutput(dataFolder, sb.toString());

        WhatsAppChat chatToAdd;
        new WhatsAppMessageParser(chatFiles[0], chat).parseFullFile();
        for(File file : chatFiles) {
            chatToAdd = new WhatsAppChat(file.getName());
            System.out.println("Using file " + file.getName() + " with size: " + file.length() + " bytes.");
            new WhatsAppMessageParser(file, chatToAdd).parseFullFile();
            System.err.println("This chat's earliest message is from: " + chatToAdd.getRegularMessages().get(0).getDateTime().toString());
            if (!chatFiles[0].equals(file)) {
                chat = chat.addChat(chatToAdd);
            }
        }
        io.writeToFile("chat", chat.toString());
        analyzeChat();
    }

    private static void analyzeChat() {
        timer.stopStart("Parsing");
        WordStats.countMessages(chat, io);
        timer.stopStart("Counting");

        wordOccurrencePerAuthor = WordStats.wordOccurrenceMapPerAuthor(chat);

        io.output("\nWeergave van het gezegde per persoon in aantal bits (werkt niet helaas)");
        Stats.makeTopDoubleN(WordStats.informationPerAuthor(wordOccurrencePerAuthor).entrySet(), 0, "Alle woorden van", "kunnen worden verkort tot","bits", io);

        new WordStats(io, chat).allStats(wordOccurrencePerAuthor);
        timer.stopStart("WordStats");
        new Charts(io, chat).allCharts();
        timer.stopStart("Charting");
        HashMap<String, Integer> deletedMessagesPerAuthor = chat.getAuthorList().stream().collect(Collectors.toMap(author -> author, author -> 0, (a, b) -> b, HashMap::new));
        chat.getDeletedMessages().forEach(message -> deletedMessagesPerAuthor.replace(message.getAuthor(), deletedMessagesPerAuthor.get(message.getAuthor()) + 1));

        io.output("\n\nVerwijderde berichten:");
        Stats.makeTopIntegerN(deletedMessagesPerAuthor.entrySet(), 0, "", "heeft", "berichten verwijderd.", io);
        io.output("Admins hebben zoveel berichten verwijderd: " + chat.getAdminDeletedMessages().size());

        timer.stop("Calculating other stats");
    }
}

