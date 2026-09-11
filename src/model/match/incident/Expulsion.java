package model.match.incident;

import model.person.player.Player;

import java.io.Serial;

public class Expulsion extends Incident {

    @Serial
    private static final long serialVersionUID = 1L;
    private Player player;
    private boolean doubleYellow;

    public Expulsion(int minute, Player player, boolean doubleYellow) {
        super(minute);
        this.player = player;
        this.doubleYellow = doubleYellow;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isDoubleYellow() {
        return doubleYellow;
    }

}