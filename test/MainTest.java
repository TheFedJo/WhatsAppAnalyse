import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parse.WhatsAppMessageParser;
import util.Math;


import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    WhatsAppMessageParser parser = new WhatsAppMessageParser();


    @BeforeEach
    void setup() {

    }

    @Test
    void testRound() {
        assertSame("gaming", "gaming");
        assertEquals(Math.round(65.3242, 2), 65.32, 0.001);
        assertEquals(Math.round(65.5151, 2), 65.52, 0.0001);





    }
}
