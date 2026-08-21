package model.match.incident;

import model.person.player.Player;

public class Expulsion extends Incident {

    private Player player;

    public Expulsion(int minute, Player player) {
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