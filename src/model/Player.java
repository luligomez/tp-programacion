package model;

import java.time.LocalDate;

public abstract class Player extends Person {
    private Position position;
    private int rating;
    private int matchesPlayed;
    private int expulsions;

    public Player(String firstName, String lastName, LocalDate birthDate,
                  String documentType, String documentNumber,
                  Position position, int rating,
                  int matchesPlayed, int expulsions) {

        super(firstName, lastName, birthDate, documentType, documentNumber);

        this.position = position;
        this.rating = rating;
        this.matchesPlayed = matchesPlayed;
        this.expulsions = expulsions;
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

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getExpulsions() {
        return expulsions;
    }

    public void setExpulsions(int expulsions) {
        this.expulsions = expulsions;
    }
}