package model;

public class Change extends Incident {

    private Player playerOut;
    private Player playerIn;

    public Change(int minute, Player playerOut, Player playerIn) {
        super(minute);
        this.playerOut = playerOut;
        this.playerIn = playerIn;
    }

    public Player getPlayerOut() {
        return playerOut;
    }

    public void setPlayerOut(Player playerOut) {
        this.playerOut = playerOut;
    }

    public Player getPlayerIn() {
        return playerIn;
    }

    public void setPlayerIn(Player playerIn) {
        this.playerIn = playerIn;
    }
}