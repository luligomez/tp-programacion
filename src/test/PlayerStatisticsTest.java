package test;

import model.match.Match;
import model.match.PlayerParticipation;
import model.person.Position;
import model.person.player.FieldPlayer.FieldPlayer;
import model.person.player.FieldPlayer.FieldPlayerAttributes;
import model.person.player.FieldPlayer.FieldPlayerCareerStats;
import model.person.player.TournamentStats;
import model.team.Team;

import java.time.LocalDate;

public class PlayerStatisticsTest {

    public static void main(String[] args) {

        // Create a test player
        FieldPlayerAttributes attributes = new FieldPlayerAttributes(
                70, 75, 80, 65, 85, 75, 80, 75
        );

        FieldPlayerCareerStats careerStats =
                new FieldPlayerCareerStats(0, 0, 0, 0, 0, 0);

        FieldPlayer player = new FieldPlayer(
                "Test Player",
                LocalDate.of(2000, 1, 1),
                "DNI",
                "12345678",
                Position.FORWARD,
                attributes,
                careerStats
        );

        // Create a test match without simulating a tournament
        Match match = new Match(null, null, null,
                (model.place.Stadium) null) {

            @Override
            public Team getWinner() {
                return null;
            }
        };

        // Simulate a player participating for 90 minutes
        match.getPlayerParticipations().add(
                new PlayerParticipation(player, true, 0, 90)
        );

        // Register statistics for the first time
        match.registerPlayerStatistics();

        TournamentStats stats = player.getTournamentStats();

        int firstMatches = stats.getMatchesPlayed();
        int firstMinutes = stats.getMinutesPlayed();

        // Try registering the same match again
        match.registerPlayerStatistics();

        int secondMatches = stats.getMatchesPlayed();
        int secondMinutes = stats.getMinutesPlayed();

        System.out.println("First registration: "
                + firstMatches + " matches, "
                + firstMinutes + " minutes");

        System.out.println("Second registration: "
                + secondMatches + " matches, "
                + secondMinutes + " minutes");

        if (firstMatches == 1 && firstMinutes == 90
                && secondMatches == 1 && secondMinutes == 90) {

            System.out.println("TEST PASSED: Statistics were not duplicated.");

        } else {
            throw new AssertionError(
                    "TEST FAILED: Player statistics are incorrect."
            );
        }
    }
}