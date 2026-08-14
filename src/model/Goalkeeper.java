package model;

import java.time.LocalDate;

public class Goalkeeper extends Player {

    private int reflexes;
    private int aerialGame;
    private int positioning;
    private int closingDown;
    private int handling;
    private int footwork;

    private int goalsReceived;
    private int penaltiesReceived;
    private int penaltiesSaved;

    public Goalkeeper(String firstName, String lastName, LocalDate birthDate,
                      String documentType, String documentNumber,
                      int rating, int matchesPlayed, int expulsions,
                      int reflexes, int aerialGame, int positioning,
                      int closingDown, int handling, int footwork,
                      int goalsReceived, int penaltiesReceived,
                      int penaltiesSaved) {

        super(firstName, lastName, birthDate, documentType, documentNumber,
                Position.GOALKEEPER, rating, matchesPlayed, expulsions);

        this.reflexes = reflexes;
        this.aerialGame = aerialGame;
        this.positioning = positioning;
        this.closingDown = closingDown;
        this.handling = handling;
        this.footwork = footwork;
        this.goalsReceived = goalsReceived;
        this.penaltiesReceived = penaltiesReceived;
        this.penaltiesSaved = penaltiesSaved;
    }

    public int getReflexes() {
        return reflexes;
    }

    public void setReflexes(int reflexes) {
        this.reflexes = reflexes;
    }

    public int getAerialGame() {
        return aerialGame;
    }

    public void setAerialGame(int aerialGame) {
        this.aerialGame = aerialGame;
    }

    public int getPositioning() {
        return positioning;
    }

    public void setPositioning(int positioning) {
        this.positioning = positioning;
    }

    public int getClosingDown() {
        return closingDown;
    }

    public void setClosingDown(int closingDown) {
        this.closingDown = closingDown;
    }

    public int getHandling() {
        return handling;
    }

    public void setHandling(int handling) {
        this.handling = handling;
    }

    public int getFootwork() {
        return footwork;
    }

    public void setFootwork(int footwork) {
        this.footwork = footwork;
    }

    public int getGoalsReceived() {
        return goalsReceived;
    }

    public void setGoalsReceived(int goalsReceived) {
        this.goalsReceived = goalsReceived;
    }

    public int getPenaltiesReceived() {
        return penaltiesReceived;
    }

    public void setPenaltiesReceived(int penaltiesReceived) {
        this.penaltiesReceived = penaltiesReceived;
    }

    public int getPenaltiesSaved() {
        return penaltiesSaved;
    }

    public void setPenaltiesSaved(int penaltiesSaved) {
        this.goalsReceived = goalsReceived;
        this.penaltiesSaved = penaltiesSaved;
    }
}