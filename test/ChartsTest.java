import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ChartsTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void testTimeStuff() {
        System.out.println(LocalTime.now().getHour() + "   " + LocalTime.now());
    }

}