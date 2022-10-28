package parse;

import java.util.ArrayList;

public class WhatsAppChat {
    private final ArrayList<WhatsAppMessage> messages;
    private ArrayList<WhatsAppMessage> deletedMessages;
    private ArrayList<WhatsAppMessage> adminDeletedMessages;
    private boolean isOpen;

    public WhatsAppChat() {
        this.messages = new ArrayList<>();
        this.adminDeletedMessages = null;
        this.deletedMessages = null;
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
        return messages;
    }

    public ArrayList<WhatsAppMessage> getDeletedMessages() {
        if(deletedMessages == null) {
            parseDeletedMessages();
        }
        return deletedMessages;
    }

    private void parseDeletedMessages(){
        deletedMessages = new ArrayList<WhatsAppMessage>();
        adminDeletedMessages = new ArrayList<WhatsAppMessage>();
        for (WhatsAppMessage message : messages) {
            String content = message.getMessage();
            if (content.equals("Dit bericht is verwijderd")) {
                deletedMessages.add(message);
            } else if (content.equals("null")) {
                adminDeletedMessages.add(message);
            }
        }
    }


}
