package model.person.player.FieldPlayer;

import model.person.Position;
import model.person.player.Attributes;

public class FieldPlayerAttributes extends Attributes {
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

    @Override
    public double calculateScore(Position position) {
        switch (position) {
            case DEFENDER:
                return (TACKLING * 0.30 + SPEED * 0.10 + SKILL * 0.08 + HEADING * 0.15
                        + FINISHING * 0.02 + SHOTPOWER * 0.05 + GAMEVISION * 0.15 + PHYSICALRESISTANCE * 0.15);
            case MIDFIELDER:
                return (TACKLING * 0.12 + SPEED * 0.10 + SKILL * 0.20 + HEADING * 0.05
                        + FINISHING * 0.10 + SHOTPOWER * 0.08 + GAMEVISION * 0.25 + PHYSICALRESISTANCE * 0.10);
            case FORWARD:
                return (TACKLING * 0.03 + SPEED * 0.15 + SKILL * 0.15 + HEADING * 0.12
                        + FINISHING * 0.30 + SHOTPOWER * 0.15 + GAMEVISION * 0.08 + PHYSICALRESISTANCE * 0.02);
            default:
                throw new IllegalArgumentException("Invalid position for FieldPlayer: " + position);
        }
    }
}
