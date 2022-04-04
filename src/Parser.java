import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class Parser {
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|[1][0-2])-[0-9]+\\s+(0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])", Pattern.CASE_INSENSITIVE);
    private static final Pattern AUTHOR_NAME_PATTERN = Pattern.compile("([a-záàâãéëèêíïóôõöúçñ ]+( [a-záàâãéëèêíïóôõöúçñ ]+)+)|([a-záàâãéëèêíïóôõöúçñ ]+)|(\\+[0-9]+\\s+\\d\\s+[0-9]+)", Pattern.CASE_INSENSITIVE);
    private BufferedReader bufferedReader;
    private final ArrayList<WhatsAppMessage> messageList;

    public Parser(File file, ArrayList<WhatsAppMessage> messageList) {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }
        this.messageList = messageList;
    }

    private String nextLine() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void parseFullFile() {
        int messageCount = 0;
        WhatsAppMessage lastMessage = null;
        for (String line = nextLine(); line != null; line = nextLine()) {
            line = line.replace("\u200E", "");              // Replace ‎ with empty, function is none
            switch (messageType(line)) {
                case MessageType.STANDARD:
                    lastMessage = lineToWAMessage(line);
                    break;
                case MessageType.ADD:
                    lastMessage = toADDMessage(line);
                    break;
                case MessageType.JOIN:
                    lastMessage = toJOINMessage(line);
                    break;
                case MessageType.KICK:
                    lastMessage = toKICKMessage(line);
                    break;
                case MessageType.EDIT_PHOTO:
                    lastMessage = toEDIT_PHOTOMessage(line);
                    break;
                case MessageType.EDIT_DESCRIPTION:
                    lastMessage = toEDIT_DESCRIPTIONMessage(line);
                    break;
                case MessageType.GENESIS:
                    lastMessage = toGENESISMessage(line);
                    break;
                case MessageType.LEAVE:
                    lastMessage = toLEAVEMessage(line);
                    break;
                case MessageType.OTHER:
                    if(lastMessage == null) {
                        continue;
                    }
                    lastMessage.appendMessage(line);
                    continue;
                default:
                    throw new IllegalStateException("Unexpected value: " + messageType(line));
            }
            messageList.add(lastMessage);
            messageCount++;
           // System.out.println("Message " + messageCount + " processed as type: " +lastMessage.getMessageType());

        }
        System.out.println("Parsing done.");
    }

    private WhatsAppMessage toEDIT_DESCRIPTIONMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), MessageType.EDIT_DESCRIPTION, rest);
    }

    private WhatsAppMessage toEDIT_PHOTOMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), MessageType.EDIT_PHOTO, rest);
    }

    private WhatsAppMessage toKICKMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), MessageType.KICK, rest);
    }

    private static WhatsAppMessage toLEAVEMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), MessageType.LEAVE, rest);
    }

    private static WhatsAppMessage lineToWAMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        String author = rest.split(": ")[0];
        String text = rest.split(": ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), author, MessageType.STANDARD, text);
    }

    private static WhatsAppMessage toGENESISMessage(String line) {
        return new WhatsAppMessage(retrieveDateTime(line), null, MessageType.GENESIS, line.split(" - ", 2)[1]);
    }

    public static WhatsAppMessage toADDMessage(String line) {
        String rest = line.split(" - ")[1];
        return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), MessageType.ADD, rest);
    }

    public static WhatsAppMessage toJOINMessage(String line) {
        String rest = line.split(" - ")[1];
        String[] splitline = rest.split("\\s+");
        StringBuilder author = new StringBuilder().append(splitline[0]);
        for (int i = 1; (!Objects.equals(splitline[i], "neemt")); i++) {
            author.append(" ").append(splitline[i]);
        }
        return new WhatsAppMessage(retrieveDateTime(line), author.toString(), MessageType.JOIN, rest);

    }

    public static boolean startsWithDateTime(String line) {
        return DATE_TIME_PATTERN.matcher(line.split(" - ")[0]).matches();

    }

    public static LocalDateTime retrieveDateTime (String line) {
        String time = line.split(" - ")[0];
        return LocalDateTime.of(2000 + Integer.parseInt(time.substring(6,8)),
                Integer.parseInt(time.substring(3,  5)),
                Integer.parseInt(time.substring(0,  2)),
                Integer.parseInt(time.substring(9,  11)),
                Integer.parseInt(time.substring(12, 14)));
    }

    public static String extractAuthorFromRest(String rest) {
        String[] splitline = rest.split("\\s+");
        StringBuilder author = new StringBuilder();
        for (int i = 0; (!Objects.equals(splitline[i], "heeft") && !Objects.equals(splitline[i], "hebt")); i++) {
            if(i != 0) {
                author.append(" ");
            }
            author.append(splitline[i]);
        }
        return author.toString();
    }

    /**
     *
     * @param input the line to be parsed
     * @return messageType
     */
    public static MessageType messageType(String input) {
        if (startsWithDateTime(input)) {
            String rest = input.split(" - ", 2)[1];
            if (rest.contains(": ") && rest.split(": ")[0].length() > 0 && AUTHOR_NAME_PATTERN.matcher(rest.split(": ")[0]).matches()) {
                return MessageType.STANDARD;
            } else if (rest.contains("end-to-end") || rest.contains(" de groep ")){
                return MessageType.GENESIS;
            } else if (input.contains("toegevoegd")){
                return MessageType.ADD;
            } else if (input.contains("uitnodiging")){
                return MessageType.JOIN;
            } else if (input.contains("groepsomschrijving")){
                return MessageType.EDIT_DESCRIPTION;
            } else if (input.contains("groepsafbeelding")){
                return MessageType.EDIT_PHOTO;
            } else if (input.contains("verwijderd")){
                return MessageType.KICK;
            } else if (input.contains("verlaten")){
                return MessageType.LEAVE;
            } else {
                return MessageType.OTHER;
            }
        } else {
            return MessageType.OTHER;
        }
    }








}
