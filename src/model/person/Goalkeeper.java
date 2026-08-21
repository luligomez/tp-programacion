package model.person;

import java.time.LocalDate;

public class Goalkeeper extends Player {
    //attributes
    private int reflexes;
    private int aerialGame;
    private int positioning;
    private int closingDown;
    private int handling;
    private int footwork;

    //career stats
    private int careerGoalsReceived;
    private int careerPenaltiesReceived;
    private int careerPenaltiesSaved;

    public Goalkeeper(String firstName, String lastName, LocalDate birthDate,
                      String documentType, String documentNumber,
                      int rating, int matchesPlayed, int expulsions,
                      int reflexes, int aerialGame, int positioning,
                      int closingDown, int handling, int footwork,
                      int careerGoalsReceived, int careerPenaltiesReceived,
                      int careerPenaltiesSaved) {

        super(firstName, lastName, birthDate, documentType, documentNumber,
                Position.GOALKEEPER, rating, matchesPlayed, expulsions);

        this.reflexes = reflexes;
        this.aerialGame = aerialGame;
        this.positioning = positioning;
        this.closingDown = closingDown;
        this.handling = handling;
        this.footwork = footwork;
        this.careerGoalsReceived = careerGoalsReceived;
        this.careerPenaltiesReceived = careerPenaltiesReceived;
        this.careerPenaltiesSaved = careerPenaltiesSaved;
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

    public int getCareerGoalsReceived() {
        return careerGoalsReceived;
    }

    public void setCareerGoalsReceived(int careerGoalsReceived) {
        this.careerGoalsReceived = careerGoalsReceived;
    }

    public int getCareerPenaltiesReceived() {
        return careerPenaltiesReceived;
    }

    public void setCareerPenaltiesReceived(int careerPenaltiesReceived) {
        this.careerPenaltiesReceived = careerPenaltiesReceived;
    }

    public int getCareerPenaltiesSaved() {
        return careerPenaltiesSaved;
    }

    public void setCareerPenaltiesSaved(int careerPenaltiesSaved) {
        this.careerPenaltiesSaved = careerPenaltiesSaved;
    }
}