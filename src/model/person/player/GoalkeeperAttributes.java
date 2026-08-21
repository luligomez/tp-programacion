package model.person.player;

public class GoalkeeperAttributes {
    private final int REFLEXES;
    private final int AERIALGAME;
    private final int POSITIONING;
    private final int CLOSINGDOWN;
    private final int HANDLING;
    private final int FOOTWORK;

    public GoalkeeperAttributes(int REFLEXES, int AERIALGAME, int POSITIONING, int CLOSINGDOWN, int HANDLING, int FOOTWORK) {
        this.REFLEXES = REFLEXES;
        this.AERIALGAME = AERIALGAME;
        this.POSITIONING = POSITIONING;
        this.CLOSINGDOWN = CLOSINGDOWN;
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

    public int getCLOSINGDOWN() {
        return CLOSINGDOWN;
    }

    public int getHANDLING() {
        return HANDLING;
    }

    public int getFOOTWORK() {
        return FOOTWORK;
    }
}
