package model.match;

import model.person.player.Player;

import java.io.Serial;
import java.io.Serializable;

public class PlayerParticipation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Player player;
    private boolean starter;
    private int minuteIn;
    private int minuteOut;

    public  PlayerParticipation(Player player, boolean starter, int minuteIn, int
                                 minuteOut){
        this.player = player;
        this.starter = starter;
        this.minuteIn = minuteIn;
        this.minuteOut = minuteOut;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isStarter() {
        return starter;
    }

    public void setStarter(boolean starter) {
        this.starter = starter;
    }

    public int getMinuteIn() {
        return minuteIn;
    }

    public void setMinuteIn(int minuteIn) {
        this.minuteIn = minuteIn;
    }

    public int getMinuteOut() {
        return minuteOut;
    }

    public void setMinuteOut(int minuteOut) {
        this.minuteOut = minuteOut;
    }
}
