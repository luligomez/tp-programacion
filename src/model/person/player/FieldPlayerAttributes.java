package model.person.player;

public class FieldPlayerAttributes {
    private final int TACKLING;
    private final int SPEED;
    private final int SKILL;
    private final int HEADING;
    private final int FINISHING;
    private final int SHOTPOWER;
    private final int GAMEVISION;
    private final int PHYSICALRESISTANCE;

    public FieldPlayerAttributes(int tackling, int speed, int skill, int heading, int finishing, int shotPower, int gameVision, int physicalResistance) {
        this.TACKLING = tackling;
        this.SPEED = speed;
        this.SKILL = skill;
        this.HEADING = heading;
        this.FINISHING = finishing;
        this.SHOTPOWER = shotPower;
        this.GAMEVISION = gameVision;
        this.PHYSICALRESISTANCE = physicalResistance;
    }

    public int getTACKLING() {
        return TACKLING;
    }

    public int getSPEED() {
        return SPEED;
    }

    public int getSKILL() {
        return SKILL;
    }

    public int getHEADING() {
        return HEADING;
    }

    public int getFINISHING() {
        return FINISHING;
    }

    public int getSHOTPOWER() {
        return SHOTPOWER;
    }

    public int getGAMEVISION() {
        return GAMEVISION;
    }

    public int getPHYSICALRESISTANCE() {
        return PHYSICALRESISTANCE;
    }
}
