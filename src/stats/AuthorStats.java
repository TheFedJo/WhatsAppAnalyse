package stats;

import main.InputOutput;
import parse.WhatsAppMessage;

import java.util.ArrayList;

public class AuthorStats {
    private final ArrayList<WhatsAppMessage> whatsAppMessages;
    private final InputOutput io;
    private ArrayList<String> authorList;


    public AuthorStats (ArrayList<WhatsAppMessage> whatsAppMessages, InputOutput io) {
        this.whatsAppMessages = whatsAppMessages;
        this.io = io;
    }

    public void allAuthorStats() {
        authorList();

    }

    public ArrayList<String> authorList() {
        if (authorList == null) {
            authorList = new ArrayList<>();
            for (WhatsAppMessage message : whatsAppMessages) {
                if (!authorList.contains(message.getAuthor())) {
                    authorList.add(message.getAuthor());
                }
            }
        }
        return authorList;
    }


}
