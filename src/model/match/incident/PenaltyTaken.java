package model.match.incident;

import model.person.player.Player;

public class PenaltyTaken extends Incident {

    private static final long serialVersionUID = 1L;
    private Player player;
    private boolean scored;

    public PenaltyTaken(int minute, Player player, boolean scored) {
        super(minute);
        this.player = player;
        this.scored = scored;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }
}