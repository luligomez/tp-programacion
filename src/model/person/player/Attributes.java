package model.person.player;

import model.person.Position;

import java.io.Serial;
import java.io.Serializable;

public abstract class Attributes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public abstract double calculateScore(Position position);
}
