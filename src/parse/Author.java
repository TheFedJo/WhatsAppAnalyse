package parse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Author {
    private final String name;
    private ArrayList<Map.Entry<LocalDateTime, MessageType>> joinAndLeaveTimes;

    public Author(String name) {
        this.name = name;
    }

    public void addJoinAndLeaveTimes(WhatsAppChat chat) {
        ArrayList<Author> authorList = chat.getAuthorList();
        LocalDateTime startDate = chat.getEarliestMessageDateTime();
        LocalDateTime endDate = chat.getLatestMessageDateTime();
        ArrayList<WhatsAppMessage> messages = chat.getMessagesOfTypes(MessageType.KICK, MessageType.JOIN, MessageType.ADD, MessageType.GENESIS, MessageType.LEAVE);
        messages.addAll(chat.getMessagesByAuthor(this));




    }





    public String getName() {
        return name;
    }

    public ArrayList<Map.Entry<LocalDateTime, MessageType>> getJoinAndLeaveTimes() {
        return new ArrayList<>(joinAndLeaveTimes);
    }

    public int getLifespan() {
        ArrayList<Map.Entry<LocalDateTime, MessageType>> joinAndLeaveTimes = getJoinAndLeaveTimes();
        int i = 0;
        while (i != joinAndLeaveTimes.size()) {

        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        return getName().equals(author.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
