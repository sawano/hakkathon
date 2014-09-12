package se.sawano.akka.chicken.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se.sawano.akka.chicken.model.Result.*;

public class ResultTest {

    @Test
    public void win_should_be_worth_1() throws Exception {
        assertEquals(1, WIN.points());
    }

    @Test
    public void tie_should_be_worth_0() throws Exception {
        assertEquals(0, TIE.points());
    }

    @Test
    public void lose_should_be_worth_minus_one() throws Exception {
        assertEquals(-1, LOSE.points());
    }

    @Test
    public void crash_should_be_worth_minus_ten() throws Exception {
        assertEquals(-10, CRASH.points());
    }

    @Test
    public void should_sum_points() throws Exception {
        final long sum = sum(WIN, LOSE, CRASH);

        assertEquals(1 - 1 - 10, sum);

    }
}
