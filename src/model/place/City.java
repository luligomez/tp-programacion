package model.place;

import java.util.ArrayList;

public class City {
    private long id;
    private String name;
    private String country;
    private ArrayList<Stadium> stadiums;


    public City(long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.stadiums = new ArrayList<>();
    }

    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCountry() {
        return country;
    }

    public ArrayList<Stadium> getStadiums() {
        return stadiums;
    }

    public void addStadium(Stadium stadium){
        stadiums.add(stadium);
    }
}
