package model.place;

import java.io.Serializable;

public class Stadium implements Serializable {

    private static final long serialVersionUID = 1L;
    private long id;
    private boolean used = false;
    private String name;
    private int capacity;
    private City city;



    public Stadium(String name, int capacity, City city) {
        this(0, name, capacity, city); //llama al constructor de abajo
    }

    public Stadium(long id ,String name, int capacity, City city) {
        this.name = name;
        this.capacity = capacity;
        this.city = city;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
