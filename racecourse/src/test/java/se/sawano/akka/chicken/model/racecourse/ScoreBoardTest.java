package se.sawano.akka.chicken.model.racecourse;

import org.junit.Test;
import se.sawano.akka.chicken.model.DriverId;
import se.sawano.akka.chicken.model.RaceId;
import se.sawano.akka.chicken.model.Result;

import static org.junit.Assert.assertEquals;
import static se.sawano.akka.chicken.model.Choice.STRAIGHT;
import static se.sawano.akka.chicken.model.Choice.SWERVE;
import static se.sawano.akka.chicken.model.Result.*;

public class ScoreBoardTest {

    @Test
    public void should_calculate_scores() throws Exception {
        final ScoreBoard scoreBoard = new ScoreBoard();
        final DriverId player1 = new DriverId("John");
        final DriverId player2 = new DriverId("Jane");
        final RaceResult result1 = new RaceResult(RaceId.next(), player1, SWERVE, player2, SWERVE);
        final RaceResult result2 = new RaceResult(RaceId.next(), player1, STRAIGHT, player2, SWERVE);

        scoreBoard.registerResult(result1);
        scoreBoard.registerResult(result2);

        assertEquals(Result.sum(TIE, WIN), scoreBoard.statsFor(player1).score());
        assertEquals(Result.sum(TIE, LOSE), scoreBoard.statsFor(player2).score());

        scoreBoard.registerResult(result2);

        assertEquals(Result.sum(TIE, WIN, WIN), scoreBoard.statsFor(player1).score());
        assertEquals(Result.sum(TIE, LOSE, LOSE), scoreBoard.statsFor(player2).score());

        assertEquals(2, scoreBoard.scores().size());
    }

}
