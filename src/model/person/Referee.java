package model.person;

import model.place.Country;

import java.io.Serial;
import java.time.LocalDate;

public class Referee extends Person {
    @Serial
    private static final long serialVersionUID = 1L;
    private Country nationality;
    private int yearsOfExperience;

    public Referee(String name, LocalDate birthDate,
                   String documentType, String documentNumber,
                   Country nationality, int yearsOfExperience) {

        super(name , birthDate, documentType, documentNumber);

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