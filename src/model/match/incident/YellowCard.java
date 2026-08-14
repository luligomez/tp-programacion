package model.match.incident;

import model.person.Player;

public class YellowCard extends Incident {

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