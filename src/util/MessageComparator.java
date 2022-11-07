package util;

import parse.WhatsAppMessage;

import java.time.LocalDateTime;
import java.util.Comparator;

public class MessageComparator implements Comparator<WhatsAppMessage> {

    @Override
    public int compare(WhatsAppMessage message1, WhatsAppMessage message2) {
        LocalDateTime ldt1 = message1.getDateTime();
        LocalDateTime ldt2 = message2.getDateTime();
        if (ldt1.isBefore(ldt2)) {
            return -1;
        } else if (ldt1.isAfter(ldt2)) {
            return 1;
        } else {
            return 0;
        }
    }
}

