package parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class WhatsAppMessage {
    private final String author;
    private String message;
    private final MessageType messageType;
    private final LocalDate date;
    private final LocalTime time;

    public WhatsAppMessage(LocalDateTime time, String author, MessageType messageType, String message) {
        this.date = time.toLocalDate();
        this.time = time.toLocalTime();
        this.author = author;
        this.messageType = messageType;
        this.message = message;
    }
    public long getEpochDate() {
        return date.toEpochDay();
    }

    public LocalTime getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(getDate(), getTime());
    }

    public void appendMessage(String nextLine) {
        this.message = getMessage() + "\n" + nextLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhatsAppMessage message1 = (WhatsAppMessage) o;

        if (!getDate().equals(message1.getDate())) return false;
        if (!getTime().equals(message1.getTime())) return false;
        if (getAuthor() != null ? !getAuthor().equals(message1.getAuthor()) : message1.getAuthor() != null)
            return false;
        if (!getMessage().equals(message1.getMessage())) return false;
        return getMessageType() == message1.getMessageType();
    }

    @Override
    public String toString() {
        if(messageType == MessageType.STANDARD) {
            return date.format(DateTimeFormatter.ofPattern("dd-MM-yy")) +
                    " " + time.toString() + " - " + author + ": " + message;
        } else {
            return date.format(DateTimeFormatter.ofPattern("dd-MM-yy")) +
                    " " + time.toString() + " - " + message;
        }
    }

    public LocalDate getDate() {
        return date;
    }
}
