import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class WordStats {
    private final InputOutput io;

    public WordStats(InputOutput io) {
        this.io = io;
    }

    public void allStats(Map<String, Map<String, Integer>> authorWordMap) {
        generalWordTopTen(authorWordMap);
        wordCountPerAuthorAndFavoriteWords(authorWordMap);
    }

    protected static ArrayList<String> createAuthorList(ArrayList<WhatsAppMessage> whatsAppMessages) {
        ArrayList<String> authors = new ArrayList<>();
        for (WhatsAppMessage whatsAppMessage : whatsAppMessages) {
            if(!authors.contains(whatsAppMessage.getAuthor())) {
                authors.add(whatsAppMessage.getAuthor());
            }
        }
        return authors;
    }

    protected static Map<String, Integer> calcMessageAmountPerAuthor(ArrayList<String> authorList, ArrayList<WhatsAppMessage> messages) {
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

    protected static double shannonInformation(double[] frequencies) {
        double information = 0;
        for (double frequency : frequencies) {
            information += frequency * (Math.log((double) 1 / frequency) / Math.log(2));
        }
        return information;
    }

    protected static double[] occurrenceSetToFrequencyList(Collection<Integer> occurrences) {
        int size = occurrences.size();
        double[] result = new double[size];
        int i = 0;
        for (int occurrence : occurrences) {
            result[i] = (double) occurrence / size;
            i++;
        }
        return result;
    }

    protected static Map<String, Map<String, Integer>> wordOccurrenceMapPerAuthor(ArrayList<WhatsAppMessage> whatsAppMessages, ArrayList<String> authorList) {
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

    protected static Map<String, Double> informationPerAuthor(Map<String, Map<String, Integer>> wordOccurrenceMap) {
        Map<String, Double> result = new HashMap<>();
        for (String author : wordOccurrenceMap.keySet()) {
            result.put(author, shannonInformation(occurrenceSetToFrequencyList(wordOccurrenceMap.get(author).values())));
        }
        return result;
    }

    protected static String[] stringToWordsArray(String message) {
        return message.split("[\\s+|:;\"'.,?]+");
    }

    protected static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected static void countMessages(ArrayList<WhatsAppMessage> messages, InputOutput io) {
        io.output("In totaal zijn er " + messages.size() + " berichten gestuurd. \nDit is de ranglijst per persoon:");
        Main.authorList = createAuthorList(messages);
        Main.messageCountPerAuthor =  calcMessageAmountPerAuthor(Main.authorList, messages);

        Main.sortedAuthorMessageCountEntries = SortMethods.mergeSort(new ArrayList(Main.messageCountPerAuthor.entrySet()), new EntryComparator());
        int i = 1;
        for (Map.Entry<String, Integer> entry : Main.sortedAuthorMessageCountEntries) {
            io.output(i + ". " + entry.getKey() + " met " + entry.getValue() + " berichten.");
            i++;
        }

    }

    private void generalWordTopTen(Map<String, Map<String, Integer>> authorWordMap) {
        Map<String, Integer> generalWordMap = new HashMap<>();
        for (Map<String, Integer> wordMap : authorWordMap.values()) {
            for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
                if (!generalWordMap.containsKey(entry.getKey())) {
                    generalWordMap.put(entry.getKey(), entry.getValue());
                } else {
                    generalWordMap.replace(entry.getKey(), generalWordMap.get(entry.getKey()) + entry.getValue());
                }
            }
        }
        ArrayList<Map.Entry> entryList = new ArrayList<>(generalWordMap.entrySet());
        entryList = SortMethods.mergeSort(entryList, new EntryComparator());
        long totalWordCount = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            totalWordCount +=  entry.getValue();
        }
        io.output("\nDe meest gebruikte woorden zijn (van " + totalWordCount + " totaal):");
        for (int i = 1; i <= 10; i++) {
            io.output(i + ". " + entryList.get(i - 1).getKey() + ", " + entryList.get(i - 1).getValue() + " keer.");
        }
    }

    private void wordCountPerAuthorAndFavoriteWords(Map<String, Map<String, Integer>> authorWordMap) {
        Map<String, ArrayList<Map.Entry<String, Integer>>> favoriteWordsPerAuthor = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> authorWordEntry : authorWordMap.entrySet()) {
            ArrayList<Map.Entry<String, Integer>> sortedFavoriteWords = SortMethods.mergeSort(new ArrayList<>(authorWordEntry.getValue().entrySet()), new EntryComparator());
            favoriteWordsPerAuthor.put(authorWordEntry.getKey(), new ArrayList<>(sortedFavoriteWords.subList(0, Math.min(10, sortedFavoriteWords.size()))));
        }
        Map<String, Integer> wordCountPerAuthor = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> authorWordEntry : authorWordMap.entrySet()) {
            int countForCurrentAuthor = 0;
            for(int wordCount : authorWordEntry.getValue().values()) {
                countForCurrentAuthor += wordCount;
            }
            wordCountPerAuthor.put(authorWordEntry.getKey(), countForCurrentAuthor);
        }
        ArrayList<Map.Entry<String, Integer>> totalWordCountPerAuthor = SortMethods.mergeSort(new ArrayList<>(wordCountPerAuthor.entrySet()), new EntryComparator());
        int i;
        for (Map.Entry<String, Integer> entry : totalWordCountPerAuthor) {
            io.output("\n" + entry.getKey() + " heeft " + entry.getValue() + " woorden uitgekraamd.\nDit zijn de favorieten:");
            i = 1;
            for (Map.Entry<String, Integer> wordPlusCount : favoriteWordsPerAuthor.get(entry.getKey())) {
                io.output(i + ". " + wordPlusCount.getKey() + ", " + wordPlusCount.getValue() + " keer gezegd.");
                i++;
            }
        }
    }
}
