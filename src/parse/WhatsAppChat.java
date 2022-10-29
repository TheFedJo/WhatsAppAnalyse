package parse;

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
            messages.add(message);
        }
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
        deletedMessages = new ArrayList<WhatsAppMessage>();
        adminDeletedMessages = new ArrayList<WhatsAppMessage>();
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
}
