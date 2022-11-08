package parse;

import util.MessageComparator;
import util.SortMethods;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class WhatsAppChat {
    private final ArrayList<WhatsAppMessage> messages;
    private ArrayList<WhatsAppMessage> deletedMessages;
    private ArrayList<WhatsAppMessage> adminDeletedMessages;
    private ArrayList<WhatsAppMessage> regularMessages;
    private ArrayList<String> authorList;
    private boolean isOpen;
    private final String name;

    public WhatsAppChat(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
        this.adminDeletedMessages = null;
        this.deletedMessages = null;
        this.authorList = null;
        this.isOpen = true;
    }
    public void addMessage(WhatsAppMessage message) {
        if (isOpen) {
            add(message);
        }
    }

    private void add(WhatsAppMessage message) {
        messages.add(message);
    }

    private void addMessages(ArrayList<WhatsAppMessage> messages) {
        if(isOpen) {
            for (WhatsAppMessage message : messages) {
                add(message);
            }
        }
    }
    public void sortMessages() {
        SortMethods.mergeSort(messages, new MessageComparator());
    }

    public void close() {
        this.isOpen = false;
    }

    public ArrayList<WhatsAppMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public ArrayList<WhatsAppMessage> getDeletedMessages() {
        if(deletedMessages == null) {
            parseDeletedMessages();
        }
        return new ArrayList<>(deletedMessages);
    }

    public ArrayList<WhatsAppMessage> getAdminDeletedMessages() {
        if(adminDeletedMessages== null) {
            parseDeletedMessages();
        }
        return new ArrayList<>(adminDeletedMessages);
    }

    private void parseDeletedMessages(){
        deletedMessages = new ArrayList<>();
        adminDeletedMessages = new ArrayList<>();
        for (WhatsAppMessage message : messages) {
            String content = message.getMessage();
            if (content.equals("Dit bericht is verwijderd")  || content.equals("U hebt dit bericht verwijderd")) {
                deletedMessages.add(message);
            } else if (content.equals("null")) {
                adminDeletedMessages.add(message);
            }
        }
    }

    public ArrayList<WhatsAppMessage> getRegularMessages() {
        if(regularMessages == null) {
            parseRegularMessages();
        }
        return new ArrayList<>(regularMessages);
    }

    private void parseRegularMessages() {
        regularMessages = new ArrayList<>(messages);
        regularMessages.removeIf(message -> message.getMessageType() != MessageType.STANDARD);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getAuthorList() {
        if (authorList == null) {
            createAuthorList();
        }
        return new ArrayList<>(authorList);
    }

    private void createAuthorList() {
        authorList = new ArrayList<>();
        for (WhatsAppMessage message : getRegularMessages()) {
            if(!authorList.contains(message.getAuthor())) {
                authorList.add(message.getAuthor());
            }
        }
    }

    public LocalDateTime getEarliestMessageDateTime() {
        WhatsAppMessage earliestMessage = this.getRegularMessages().get(0);
        for (WhatsAppMessage message : this.getRegularMessages()) {
            if (message.getDateTime().isBefore(earliestMessage.getDateTime())) {
                earliestMessage = message;
            }
        }
        return earliestMessage.getDateTime();
    }

    public LocalDateTime getLatestMessageDateTime() {
        ArrayList<WhatsAppMessage> messages = this.getRegularMessages();
        WhatsAppMessage latestMessage = messages.get(messages.size() - 1);
        for (WhatsAppMessage message : this.getRegularMessages()) {
            if (message.getDateTime().isAfter(latestMessage.getDateTime())) {
                latestMessage = message;
            }
        }
        return latestMessage.getDateTime();
    }

    public WhatsAppChat addChat(WhatsAppChat chat) {
        WhatsAppChat result = new WhatsAppChat(this.name + " + " + chat.getName());
        ArrayList<WhatsAppMessage> messages1 = this.getMessages();
        ArrayList<WhatsAppMessage> messages2 = chat.getMessages();
        for(WhatsAppMessage message1 : messages1) {
            messages2.removeIf(message1::equals);
        }
        result.addMessages(messages1);
        result.addMessages(messages2);
        result.close();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append("\nAuthors:");
        for (String author : this.getAuthorList()) {
            sb.append("\n").append(author);
        }
        for (WhatsAppMessage message : this.messages) {
            sb.append("\n").append(message);
        }
        return sb.toString();
    }
}
