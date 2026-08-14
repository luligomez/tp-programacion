package model.person;

import model.place.Country;

import java.time.LocalDate;

public class Referee extends Person {
    private Country nationality;
    private int yearsOfExperience;

    public Referee(String firstName, String lastName, LocalDate birthDate,
                   String documentType, String documentNumber,
                   Country nationality, int yearsOfExperience) {

        super(firstName, lastName, birthDate, documentType, documentNumber);

        this.nationality = nationality;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}