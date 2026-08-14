package model.place;

public class Stadium {
    private boolean used;
    private String name;
    private int capacity;
    private City city;


    public Stadium (String name, int capacity, City city) {
        this.used = false;
        this.name = name;
        this.capacity = capacity;
        this.city = city;
    }

    public boolean isUsed() {
        return used;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getCapacity() {
        return capacity;
    }

    public City getCity() {
        return city;
    }

    public void setUsed(boolean used){
        this.used = used;
    }
}
