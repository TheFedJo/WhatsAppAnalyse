import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WhatsAppMessageParser {
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|[1][0-2])-[0-9]+\\s+(0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])", Pattern.CASE_INSENSITIVE);
    private static final Pattern AUTHOR_NAME_PATTERN = Pattern.compile("([a-záàâãéëèêíïóôõöúçñ ]+( [a-záàâãéëèêíïóôõöúçñ ]+)+)|([a-záàâãéëèêíïóôõöúçñ ]+)|(\\+[0-9]+\\s+\\d\\s+[0-9]+)", Pattern.CASE_INSENSITIVE);
    private BufferedReader bufferedReader;
    private final ArrayList<WhatsAppMessage> messageList;

    public WhatsAppMessageParser(File file, ArrayList<WhatsAppMessage> messageList) {
        this.messageList = messageList;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            this.parseFullFile();
        } catch (Exception e) {
            System.out.println("Something went wrong:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public WhatsAppMessageParser() {
        // for testing purposes
        this.messageList = new ArrayList<>();
    }

    private String nextLine() throws IOException {
        return bufferedReader.readLine();
    }

    public void parseFullFile() throws IOException {
        MessageType messageType;
        WhatsAppMessage lastMessage = new WhatsAppMessage(LocalDateTime.now(),null, null,null);
        for (String line = nextLine(); line != null; line = nextLine()) {
            line = line.replace("\u200E", "");              // Replace ‎ with empty, this pops up in some messages and is just annoying to deal with
            messageType = messageType(line);
            switch (messageType) {
                case STANDARD:
                    lastMessage = lineToWAMessage(line);
                    break;
                case ADD:
                case LEAVE:
                case JOIN:
                case KICK:
                case EDIT_PHOTO:
                case EDIT_DESCRIPTION:
                    lastMessage = toDefaultAdminMessage(line, messageType);
                    break;
                case CODE_CHANGE:
                    lastMessage = toCODE_CHANGEMessage(line);
                    break;
                case GENESIS:
                    lastMessage = toGENESISMessage(line);
                    break;
                case NEWLINE:
                    lastMessage.appendMessage(line);
                    continue;
                case OTHER:
                    System.out.println("Unexpected value, message discarded:\n" + line);
                    continue;
            }
            messageList.add(lastMessage);
        }
        System.out.println("Parsing done.");
    }

    protected static WhatsAppMessage toDefaultAdminMessage(String line, MessageType type) {  // case EDIT_DESCRIPTION, EDIT_PHOTO, KICK, LEAVE, ADD, JOIN
        String rest = line.split(" - ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), type, rest);
    }

    protected static WhatsAppMessage toCODE_CHANGEMessage(String line) {
        return new WhatsAppMessage(retrieveDateTime(line), null, MessageType.CODE_CHANGE, line.split(" - ", 2)[1]);
    }

    protected static WhatsAppMessage lineToWAMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        String author = rest.split(": ")[0];
        String text = rest.split(": ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), author, MessageType.STANDARD, text);
    }

    protected static WhatsAppMessage toGENESISMessage(String line) {
        return new WhatsAppMessage(retrieveDateTime(line), null, MessageType.GENESIS, line.split(" - ", 2)[1]);
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
        return IntStream.iterate(1,
                i -> !Objects.equals(splitline[i], "heeft") && !Objects.equals(splitline[i], "hebt") && !Objects.equals(splitline[i], "neemt"),
                i -> i + 1).mapToObj(i -> " " + splitline[i]).collect(Collectors.joining("", splitline[0], ""));
    }

    /**
     * Retrieves the type of message from a particular line (OTHER will be appended to the previous message)
     * @param line the line to be parsed
     * @return messageType
     */
    public static MessageType messageType(String line) {
        if (startsWithDateTime(line)) {
            String rest = line.split(" - ", 2)[1];
            if (rest.contains(": ") && rest.split(": ")[0].length() > 0 && AUTHOR_NAME_PATTERN.matcher(rest.split(": ")[0]).matches()) {
                return MessageType.STANDARD;
            } else if (rest.contains("end-to-end") || rest.contains(" de groep ")){
                return MessageType.GENESIS;
            } else if (line.contains("toegevoegd")){
                return MessageType.ADD;
            } else if (line.contains("uitnodiging")){
                return MessageType.JOIN;
            } else if (line.contains("groepsomschrijving")){
                return MessageType.EDIT_DESCRIPTION;
            } else if (line.contains("groepsafbeelding")){
                return MessageType.EDIT_PHOTO;
            } else if (line.contains("verwijderd")){
                return MessageType.KICK;
            } else if (line.contains("verlaten")){
                return MessageType.LEAVE;
            } else if (line.contains("beveiligingscode")){
                return MessageType.CODE_CHANGE;
            } else {
                System.out.println("Message not recognized, therefore discarded:\n"  + line);
                return MessageType.OTHER;
            }
        } else {
            return MessageType.NEWLINE;
        }
    }
}