import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

        File inputFile = new File(String.valueOf(Paths.get("data", "chats", "DGM30-03-22.txt")));
        System.out.println("Using file " + inputFile.getName() + " with size: " + inputFile.length() + " bytes.");
        InputOutput io = new InputOutput(inputFile);
        io.setFileName("default");

        new WhatsAppMessageParser(inputFile, whatsAppMessages).parseFullFile();
        filterToStandardMessagesOnly(whatsAppMessages);

        timer.stopStart("Parsing");

        io.output("In totaal zijn er " + whatsAppMessages.size() + " berichten gestuurd. \nDit is de ranglijst per persoon:");
        authorList = createAuthorList(whatsAppMessages);
        messageCountPerAuthor =  calcMessageAmountPerAuthor(authorList, whatsAppMessages);

        sortedAuthorMessageCountEntries = SortMethods.mergeSort(new ArrayList(messageCountPerAuthor.entrySet()), new EntryComparator());
        int i = 1;
        for (Map.Entry<String, Integer> entry : sortedAuthorMessageCountEntries) {
            io.output(i + ". " + entry.getKey() + " met " + entry.getValue() + " berichten.");
            i++;
        }

        timer.stopStart("Counting");

        wordOccurrencePerAuthor = wordOccurrenceMapPerAuthor(whatsAppMessages, authorList);
        informationPerAuthor = SortMethods.mergeSort(new ArrayList<>(informationPerAuthor(wordOccurrencePerAuthor).entrySet()), new EntryComparator());
        i = 1;
        io.output("\n");
        for (Map.Entry<String, Double> entry : informationPerAuthor) {
            io.output(i + ". " + entry.getKey() + " met " + round(entry.getValue(), 2) + " bits verschillende informatie.");
            i++;
        }

        new WordStats(io).allStats(wordOccurrencePerAuthor);

        timer.stop("Calculating other stats");
        System.out.println("Now enter your query");
    }

    static void filterToStandardMessagesOnly(ArrayList<WhatsAppMessage> messages) {
        messages.removeIf(message -> message.getMessageType() != MessageType.STANDARD);
    }

    static ArrayList<String> createAuthorList(ArrayList<WhatsAppMessage> whatsAppMessages) {
        ArrayList<String> authors = new ArrayList<>();
        for (WhatsAppMessage whatsAppMessage : whatsAppMessages) {
            if(!authors.contains(whatsAppMessage.getAuthor())) {
                authors.add(whatsAppMessage.getAuthor());
            }
        }
        return authors;
    }

    static Map<String, Integer> calcMessageAmountPerAuthor(ArrayList<String> authorList, ArrayList<WhatsAppMessage> messages) {
        Map<String, Integer> messageCount = new HashMap<>();
        for (String author : authorList) {
            messageCount.put(author, 0);
        }
        for (WhatsAppMessage whatsAppMessage : messages) {
            if (whatsAppMessage.getMessageType() == MessageType.STANDARD) {
                messageCount.replace(whatsAppMessage.getAuthor(), messageCount.get(whatsAppMessage.getAuthor()) + 1);
            }
        }
        return messageCount;
    }

    static double shannonInformation(double[] frequencies) {
        double information = 0;
        for (double frequency : frequencies) {
            information += frequency * (Math.log((double) 1 / frequency) / Math.log(2));
        }
        return information;
    }

    static double[] occurrenceSetToFrequencyList(Collection<Integer> occurrences) {
        int size = occurrences.size();
        double[] result = new double[size];
        int i = 0;
        for (int occurrence : occurrences) {
            result[i] = (double) occurrence / size;
            i++;
        }
        return result;
    }

    static Map<String, Map<String, Integer>> wordOccurrenceMapPerAuthor (ArrayList<WhatsAppMessage> whatsAppMessages, ArrayList<String> authorList) {
        Map<String, Map<String, Integer>> finalResult = new HashMap<>();
        for (String author : authorList) {
            finalResult.put(author, new HashMap<>());
        }
        String lowerCaseWord;
        for (WhatsAppMessage message : whatsAppMessages) {
            for (String word : stringToWordsArray(message.getMessage())) {
                lowerCaseWord = word.toLowerCase(Locale.ROOT);
                if (finalResult.get(message.getAuthor()).containsKey(lowerCaseWord)) {
                    finalResult.get(message.getAuthor()).replace(lowerCaseWord, finalResult.get(message.getAuthor()).get(lowerCaseWord) + 1);
                } else {
                    finalResult.get(message.getAuthor()).put(lowerCaseWord, 1);
                }
            }
        }
        return finalResult;
    }

    static Map<String, Double> informationPerAuthor (Map<String, Map<String, Integer>> wordOccurrenceMap) {
        Map<String, Double> result = new HashMap<>();
        for (String author : wordOccurrenceMap.keySet()) {
            result.put(author, shannonInformation(occurrenceSetToFrequencyList(wordOccurrenceMap.get(author).values())));
        }
        return result;
    }

    static String[] stringToWordsArray(String message) {
        return message.split("[\\s+|:;\"'.,?]+");
    }

    public static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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