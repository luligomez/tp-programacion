package model.person.player.Goalkeeper;

import model.person.Position;
import model.person.player.Attributes;

public class GoalkeeperAttributes extends Attributes {
    private final int REFLEXES;
    private final int AERIALGAME;
    private final int POSITIONING;
    private final int RUSHINGOUT;
    private final int HANDLING;
    private final int FOOTWORK;

    public GoalkeeperAttributes(int REFLEXES, int AERIALGAME, int POSITIONING, int RUSHINGOUT, int HANDLING, int FOOTWORK) {
        this.REFLEXES = REFLEXES;
        this.AERIALGAME = AERIALGAME;
        this.POSITIONING = POSITIONING;
        this.RUSHINGOUT = RUSHINGOUT;
        this.HANDLING = HANDLING;
        this.FOOTWORK = FOOTWORK;
    }

    public int getREFLEXES() {
        return REFLEXES;
    }

    public int getAERIALGAME() {
        return AERIALGAME;
    }

    public int getPOSITIONING() {
        return POSITIONING;
    }

    public int getRUSHINGOUT() {
        return RUSHINGOUT;
    }

    public int getHANDLING() {
        return HANDLING;
    }

    public int getFOOTWORK() {
        return FOOTWORK;
    }

    @Override
    public double calculateScore(Position position) {
        if(position.equals(Position.GOALKEEPER)){
            return (int) Math.round(
                    REFLEXES * 0.25 + POSITIONING * 0.20 + HANDLING * 0.20 +
                            AERIALGAME * 0.15 + FOOTWORK * 0.10 + RUSHINGOUT * 0.10
            );
        }
        throw new IllegalArgumentException("Invalid position for Goalkeeper: " + position);    }
}
