package model.match.incident;

import model.person.player.Player;

public class YellowCard extends Incident {

    private static final long serialVersionUID = 1L;
    private Player player;

    public YellowCard(int minute, Player player) {
        super(minute);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}