package model.person;

import java.time.LocalDate;

public abstract class Player extends Person {
    private Position position;
    private int rating;

    //tournament stats
    private TournamentStats tournamentStats;

    //carreer stats ///TODO CREAR GOALKEEPERCAREERSTATS Y FIELDPLAYERCAREERSTATS
    private int carreerMatchesPlayed;
    private int careerExpulsions;

    public Player(String firstName, String lastName, LocalDate birthDate,
                  String documentType, String documentNumber,
                  Position position, int rating,
                  int carreerMatchesPlayed, int careerExpulsions) {

        super(firstName, lastName, birthDate, documentType, documentNumber);

        this.position = position;
        this.rating = rating;
        this.carreerMatchesPlayed = carreerMatchesPlayed;
        this.careerExpulsions = careerExpulsions;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getCarreerMatchesPlayed() {
        return carreerMatchesPlayed;
    }

    public void setCarreerMatchesPlayed(int carreerMatchesPlayed) {
        this.carreerMatchesPlayed = carreerMatchesPlayed;
    }

    public int getCareerExpulsions() {
        return careerExpulsions;
    }

    public void setCareerExpulsions(int careerExpulsions) {
        this.careerExpulsions = careerExpulsions;
    }
}