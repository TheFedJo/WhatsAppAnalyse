import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static ArrayList<WhatsAppMessage> whatsAppMessages = new ArrayList<>();
    public static ArrayList<String> authorList;
    public static ArrayList<Map.Entry<String, Integer>> sortedAuthorMessageCountEntries = new ArrayList<>();
    public static Map<String, Integer> messagesPerAuthor;

    public static void main(String[] args) throws IOException{
        long start = System.currentTimeMillis();

        File file = new File(String.valueOf(Paths.get("data", "TK31-03-22.txt")));
        System.out.println("Using file " + file.getName() + " with size: " + file.length() + " bytes.");
        Parser parser = new Parser(file, whatsAppMessages);
        parser.parseFullFile();

        long end = System.currentTimeMillis();
        System.out.println("Parsing took " + (end - start) + " ms.");
        start = System.currentTimeMillis();

//        for (WhatsAppMessage message : whatsAppMessages) {
//            System.out.println(message);
//        }

        System.out.println("In totaal zijn er " +whatsAppMessages.size() + " berichten gestuurd. \nDit is de ranglijst per persoon:");
        authorList = createAuthorList(whatsAppMessages);
        messagesPerAuthor =  messagesPerAuthor(authorList);
        sortedAuthorMessageCountEntries.addAll(messagesPerAuthor.entrySet());

        for (int i = 0; i < sortedAuthorMessageCountEntries.size(); i++) {
            for(int j = 0; j < sortedAuthorMessageCountEntries.size() - 1; j++) {
                if(sortedAuthorMessageCountEntries.get(j).getValue() < sortedAuthorMessageCountEntries.get(j + 1).getValue()) {
                    Map.Entry<String, Integer> higher = sortedAuthorMessageCountEntries.get(j+1);
                    Map.Entry<String, Integer> lower = sortedAuthorMessageCountEntries.get(j);
                    sortedAuthorMessageCountEntries.set(j, higher);
                    sortedAuthorMessageCountEntries.set(j + 1, lower);
                }
            }
        }
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





    }

    static String allWordsForAuthor (String author, ArrayList<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            if (message)
            sb.append(" ").append(message);
        }
        return sb.toString();
    }

    static Map<String, Integer> wordOccurrenceMap (ArrayList<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(" ").append(message);
        }
        String totalMessages = sb.toString();










        return ne;



    }
}
