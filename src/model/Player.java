package model;

import java.time.LocalDate;

public class Player extends Person {
    private Incident incident;
    private Position position;
    private int rating;

    public Player(String firstName, String lastName, LocalDate birthDate,
                  String documentType, String documentNumber,
                  Position position, int rating) {

        super(firstName, lastName, birthDate, documentType, documentNumber);

        this.position = position;
        this.rating = rating;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
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
}