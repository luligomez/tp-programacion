package model.person;

public enum Position {

    //            name          short  attack  defense  foul    min  max   (min/max = players allowed on the pitch)
    GOALKEEPER("Goalkeeper", "GK", 0.0, 1.2, 2.0, 0, 11),
    DEFENDER("Defender", "DF", 0.15, 1.0, 40.0, 3, 5),
    MIDFIELDER("Midfielder", "MF", 0.6, 0.5, 35.0, 3, 6),
    FORWARD("Forward", "FW", 1.0, 0.05, 20.0, 1, 4);

    private final String displayName;
    private final String shortName;
    private final double attackWeight;    // contribution to the team's attack
    private final double defenseWeight;   // contribution to the team's defense
    private final double foulWeight;      // how likely a player in this position is to commit a foul
    private final int minOnPitch;
    private final int maxOnPitch;

    Position(String displayName, String shortName, double attackWeight, double defenseWeight,
             double foulWeight, int minOnPitch, int maxOnPitch) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.attackWeight = attackWeight;
        this.defenseWeight = defenseWeight;
        this.foulWeight = foulWeight;
        this.minOnPitch = minOnPitch;
        this.maxOnPitch = maxOnPitch;
    }

    public double getAttackWeight() {
        return attackWeight;
    }

    public double getDefenseWeight() {
        return defenseWeight;
    }

    public double getFoulWeight() {
        return foulWeight;
    }

    // Whether a tactical scheme with this many players in this position is allowed
    public boolean isAllowedOnPitch(long count) {
        return count >= minOnPitch && count <= maxOnPitch;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

}