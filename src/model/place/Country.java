package model.place;

import java.io.Serializable;

public class Country implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

