package parse;


import main.InputOutput;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WhatsAppMessageParser {
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|[1][0-2])-[0-9]+\\s+(0?[0-9]|1[0-9]|2[0-3]):(0?[0-9]|[1-5][0-9])", Pattern.CASE_INSENSITIVE);
    private BufferedReader bufferedReader;
    private final ArrayList<WhatsAppMessage> messageList;
    private WhatsAppChat chat;

    public WhatsAppMessageParser(File file, ArrayList<WhatsAppMessage> messageList) {
        this.messageList = messageList;
        this.chat = new WhatsAppChat(null);
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (Exception e) {
            System.out.println("Something went wrong:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public WhatsAppMessageParser(File file, WhatsAppChat chat) {
        this.messageList = new ArrayList<>();
        this.chat = chat;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (Exception e) {
            System.out.println("Something went wrong:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public WhatsAppMessageParser(File file, ArrayList<WhatsAppMessage> messageList, InputOutput io) {
        // for debugging
        this(file, messageList);
        this.chat = null;
        io.setFileName("chat as read");
    }

    public WhatsAppMessageParser() {
        // for testing purposes
        this.messageList = new ArrayList<>();
        this.chat = null;
    }

    private String nextLine() throws IOException {
        return bufferedReader.readLine();
    }

    public WhatsAppChat parseFullFile() {
        MessageType messageType;
        WhatsAppMessage lastMessage = new WhatsAppMessage(LocalDateTime.now(),null, null,null);
        try {
            for (String line = nextLine(); line != null; line = nextLine()) {
                line = line.replace("\u200E", "");              // Replace ‎ with empty, this pops up in some messages and is just annoying to deal with
                messageType = getMessageType(line);
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
                    case EDIT_NAME:
                    case START_CALL:
                    case EDIT_SETTINGS:
                        lastMessage = toDefaultAdminMessage(line, messageType);
                        break;
                    case ADMIN_CHANGE:
                    case CODE_CHANGE:
                    case GENESIS:
                        lastMessage = toDefaultAuthorlessMessage(line, messageType);
                        break;
                    case NEWLINE:
                        lastMessage.appendMessage(line);
                        continue;
                    case OTHER:
                        System.out.println("Unexpected value, message discarded:\n" + line);
                        continue;
                }
                messageList.add(lastMessage);
                if (!line.equals(lastMessage.toString())) {
                    System.err.println(line);
                    System.err.println(lastMessage.toString());
                }
                chat.addMessage(lastMessage);
                if (messageType != MessageType.STANDARD && lastMessage.getMessage().contains(":")) {
                    System.err.println("OK NOT GONNA HAPPEN");                                                  // what does this mean idk

                }
            //    io.output(messageType + ", " + lastMessage.getAuthor() + "\n" + lastMessage); // debug line
            }
            chat.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return chat;
    }

    protected static WhatsAppMessage toDefaultAdminMessage(String line, MessageType type) {  // case EDIT_DESCRIPTION, EDIT_PHOTO, KICK, LEAVE, ADD, JOIN
        String rest = line.split(" - ", 2)[1];
        try {
            return new WhatsAppMessage(retrieveDateTime(line), extractAuthorFromRest(rest), type, rest);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(line + "   " + type);
            e.printStackTrace();
            System.exit(1);
            return null;
        }

    }

    protected static WhatsAppMessage lineToWAMessage(String line) {
        String rest = line.split(" - ", 2)[1];
        String author = rest.split(": ")[0];
        String text = rest.split(": ", 2)[1];
        return new WhatsAppMessage(retrieveDateTime(line), author, MessageType.STANDARD, text);
    }

    protected static WhatsAppMessage toDefaultAuthorlessMessage(String line, MessageType type) {
        return new WhatsAppMessage(retrieveDateTime(line), null, type, line.split(" - ", 2)[1]);
    }

    public static boolean startsWithDateTime(String line) {
        return DATE_TIME_PATTERN.matcher(line.split(" - ")[0]).matches();
    }

    public static LocalDateTime retrieveDateTime (String line) {
        String time = line.split(" - ")[0];
        String[] timeSplit = time.split("[-\\s:]+");
        int year = Integer.parseInt(timeSplit[2]);
        if(year < 2000) {
            year += 2000;
        }
        return LocalDateTime.of(year,
                Integer.parseInt(timeSplit[1]),
                Integer.parseInt(timeSplit[0]),
                Integer.parseInt(timeSplit[3]),
                Integer.parseInt(timeSplit[4]));
    }

    public static String extractAuthorFromRest(String rest) throws ArrayIndexOutOfBoundsException {
        String[] splitline = rest.split("\\s+");
        return IntStream.iterate(1,
                i -> !Objects.equals(splitline[i], "heeft") && !Objects.equals(splitline[i], "hebt") &&
                     !Objects.equals(splitline[i], "neemt") && !Objects.equals(splitline[i], "bent")
                        && !Objects.equals(splitline[i], "is"),
                i -> i + 1).mapToObj(i -> " " + splitline[i]).collect(Collectors.joining("", splitline[0], ""));
    }

    /**
     * Retrieves the type of message from a particular line (NEWLINE will be appended to the previous message)
     * @param line the line to be parsed
     * @return messageType
     */
    public static MessageType getMessageType(String line) {
        if (startsWithDateTime(line)) {
            String rest = line.split(" - ", 2)[1];
            if (rest.contains(": ") && rest.split(": ")[0].length() > 0) {
                return MessageType.STANDARD;
            } else if (rest.contains("end-to-end") || rest.contains(" de groep ")){
                return MessageType.GENESIS;
            } else if (rest.contains("toegevoegd")){
                return MessageType.ADD;
            } else if (rest.contains("uitnodiging")){
                return MessageType.JOIN;
            } else if (rest.contains("groepsomschrijving")){
                return MessageType.EDIT_DESCRIPTION;
            } else if (rest.contains("groepsafbeelding")){
                return MessageType.EDIT_PHOTO;
            } else if (rest.contains("verwijderd")){
                return MessageType.KICK;
            } else if (line.contains("verlaten")){
                return MessageType.LEAVE;
            } else if (line.contains("beveiligingscode")){
                return MessageType.CODE_CHANGE;
            } else if (line.contains("oproep")) {
                return MessageType.START_CALL;
            } else if (line.contains("onderwerp")) {
                return MessageType.EDIT_NAME;
            } else if (line.contains("groepsinstelling")) {
                return MessageType.EDIT_SETTINGS;
            } else if (line.contains("beheerder")) {
                return MessageType.ADMIN_CHANGE;
            } else {
                return MessageType.OTHER;
            }
        } else {
            return MessageType.NEWLINE;
        }
    }
}