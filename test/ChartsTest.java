import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

class ChartsTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void testTimeStuff() {
        System.out.println(LocalTime.now().getHour() + "   " + LocalTime.now());
    }

}