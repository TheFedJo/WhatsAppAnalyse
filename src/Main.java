import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static ArrayList<WhatsAppMessage> whatsAppMessages = new ArrayList<>();
    public static ArrayList<String> authorList;
    public static ArrayList<Map.Entry<String, Integer>> sortedAuthorMessageCountEntries;
    public static Map<String, Integer> messagesPerAuthor;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        File file = new File(String.valueOf(Paths.get("data", "TK31-03-22.txt")));
        System.out.println("Using file " + file.getName() + " with size: " + file.length() + " bytes.");
        WhatsAppMessageParser whatsAppMessageParser = new WhatsAppMessageParser(file, whatsAppMessages);

        long end = System.currentTimeMillis();
        System.out.println("Parsing took " + (end - start) + " ms.");
        start = System.currentTimeMillis();

        System.out.println("In totaal zijn er " +whatsAppMessages.size() + " berichten gestuurd. \nDit is de ranglijst per persoon:");
        authorList = createAuthorList(whatsAppMessages);
        messagesPerAuthor =  messagesPerAuthor(authorList);

        sortedAuthorMessageCountEntries = sortMethods.mergeSort(new ArrayList(messagesPerAuthor.entrySet()), new EntryComparator());
        int i = 1;
        for (Map.Entry<String, Integer> entry : sortedAuthorMessageCountEntries) {
            System.out.println(i + ". " + entry.getKey() + " met " + entry.getValue() + " berichten.");
            i++;
        }

        end = System.currentTimeMillis();
        System.out.println("Counting took " + (end - start) + " ms.");
        start = System.currentTimeMillis();
















        end = System.currentTimeMillis();
        System.out.println("Information density calculation took " + (end - start) + " ms.");
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

    static Map<String, Integer> messagesPerAuthor(ArrayList<String> authorList) {
        Map<String, Integer> messageCount = new HashMap<>();
        for (String author : authorList) {
            messageCount.put(author, 0);
        }
        for (WhatsAppMessage whatsAppMessage : whatsAppMessages) {
            if (whatsAppMessage.getMessageType() == MessageType.STANDARD) {
                messageCount.put(whatsAppMessage.getAuthor(), messageCount.get(whatsAppMessage.getAuthor()) + 1);
            }
        }
        return messageCount;
    }

    static double shannonInformation(ArrayList<Double> frequencies) {
        double information = 0;
        for (double frequency : frequencies) {
            information += frequency * (Math.log((double) 1 / frequency) / Math.log(2));
        }
        return information;
    }

    static Map<String, ArrayList<String>> wordsPerAuthor (ArrayList<WhatsAppMessage> whatsAppMessages) {








        return null;





    }

    static String allWordsForAuthor (String author, ArrayList<WhatsAppMessage> whatsAppMessages) {
        StringBuilder sb = new StringBuilder();
        for (WhatsAppMessage message : whatsAppMessages) {
            if (message.getAuthor().equals(author))
            sb.append(" ").append(message);
        }
        return sb.toString();
    }

    static Map<String, Map<String, Integer>> wordOccurrenceMap (ArrayList<WhatsAppMessage> whatsAppMessages, ArrayList<String> authorList) {
        Map<String, Map<String, Integer>> finalResult = new HashMap<>();
        for (String author : authorList) {









        }
        return finalResult;
    }

}

class EntryComparator implements Comparator<Map.Entry<String, Integer>>{
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return (o1.getValue() - o2.getValue());
    }
}

class sortMethods {
     static ArrayList mergeSort(ArrayList elements, Comparator comparator) {
        if (elements.size() <= 1) {
            return elements;
        }
        ArrayList firstList = mergeSort( new ArrayList(elements.subList(0, elements.size() / 2)), comparator);
        ArrayList secondList = mergeSort( new ArrayList(elements.subList(elements.size() / 2, elements.size())), comparator);
        int fi = 0;
        int si = 0;
        ArrayList<Object> result = new ArrayList<>();
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