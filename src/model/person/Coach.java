package model.person;

import model.place.Country;

import java.time.LocalDate;

public class Coach extends Person {
    private Country nationality;
    private int titlesWon;

    public Coach(String name, LocalDate birthDate,
                 String documentType, String documentNumber,
                 Country nationality, int titlesWon) {

        super(name, birthDate, documentType, documentNumber);

        this.nationality = nationality;
        this.titlesWon = titlesWon;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public int getTitlesWon() {
        return titlesWon;
    }

    public void setTitlesWon(int titlesWon) {
        this.titlesWon = titlesWon;
    }
}

