package model.place;

import java.io.Serial;
import java.io.Serializable;

public class Stadium implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private long id;
    private boolean used = false;
    private String name;
    private int capacity;
    private City city;



    public Stadium(String name, int capacity, City city) {
        this(0, name, capacity, city); // calls the constructor below
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

    // Identified by its database id; an object not saved yet (id 0) is only equal to itself
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stadium other)) return false;
        return id != 0 && id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
