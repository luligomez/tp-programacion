package model;

public class Stadium {

    private String name;
    private City city;

    public Stadium (String name, City city){
        this.city = city;
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
