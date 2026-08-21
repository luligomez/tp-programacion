package model.person.player;

public abstract class CareerStats {
    private final int MATCHESPLAYED;
    private final int EXPULSIONS;

    public CareerStats(int matchesPlayed, int expulsions) {
        this.MATCHESPLAYED = matchesPlayed;
        this.EXPULSIONS = expulsions;
    }

    public int getMATCHESPLAYED() {
        return MATCHESPLAYED;
    }

    public int getEXPULSIONS() {
        return EXPULSIONS;
    }
}
