package model.reports.player;

import model.person.Position;
import java.time.LocalDate;

public class PlayerReportItem {

    private final String playerName;
    private final String teamName;
    private final Position position;
    private final int age;
    private final LocalDate birthDate;
    private final String documentType;
    private final String documentNumber;
    private final int rating;
    private final int matchesPlayed;
    private final int minutesPlayed;
    private final int goalsScored;
    private final int goalsConceded;
    private final double goalsConcededPerMatch;

    // Disciplinary statistics
    private final int yellowCards;
    private final int doubleYellowExpulsions;
    private final int directRedCards;

    public PlayerReportItem(
            String playerName,
            String teamName,
            Position position,
            int age,
            LocalDate birthDate,
            String documentType,
            String documentNumber,
            int rating,
            int matchesPlayed,
            int minutesPlayed,
            int goalsScored,
            int goalsConceded,
            double goalsConcededPerMatch,
            int yellowCards,
            int doubleYellowExpulsions,
            int directRedCards) {

        this.playerName = playerName;
        this.teamName = teamName;
        this.position = position;
        this.age = age;
        this.birthDate = birthDate;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.rating = rating;
        this.matchesPlayed = matchesPlayed;
        this.minutesPlayed = minutesPlayed;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
        this.goalsConcededPerMatch = goalsConcededPerMatch;
        this.yellowCards = yellowCards;
        this.doubleYellowExpulsions = doubleYellowExpulsions;
        this.directRedCards = directRedCards;
    }

    public String getPlayerName() { return playerName; }
    public String getTeamName() { return teamName; }
    public Position getPosition() { return position; }
    public int getAge() { return age; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getDocumentType() { return documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public int getRating() { return rating; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getMinutesPlayed() { return minutesPlayed; }
    public int getGoalsScored() { return goalsScored; }
    public int getGoalsConceded() { return goalsConceded; }
    public double getGoalsConcededPerMatch() {
        return goalsConcededPerMatch;
    }

    public int getYellowCards() { return yellowCards; }
    public int getDoubleYellowExpulsions() { return doubleYellowExpulsions; }
    public int getDirectRedCards() { return directRedCards; }
}