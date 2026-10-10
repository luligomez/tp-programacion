package model.match.incident;

import model.match.Match;
import model.person.player.Player;
import model.reports.MatchReport.*;

import java.io.Serial;
import java.util.Optional;

public class Substitution extends Incident {

    @Serial
    private static final long serialVersionUID = 1L;
    private Player playerOut;
    private Player playerIn;

    public Substitution(int minute, Player playerOut, Player playerIn) {
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

    @Override
    public String toString() {
        String outName = (playerOut != null) ? playerOut.getName() : "Out";
        String inName = (playerIn != null) ? playerIn.getName() : "In";
        return super.toString()+" | 🔄 Sub: Out " + outName + " ➔ In " + inName;
    }

    @Override
    public Optional<TimelineEvent> toTimelineEvent(Match match) {
        Side side = match.isTeam1Player(playerOut) ? Side.TEAM1 : Side.TEAM2;
        return Optional.of(new TimelineEvent(getMinute(), side, EventType.SUBSTITUTION,
                playerOut.getName(), playerIn.getName()));
    }

    @Override
    public Optional<PlayerMark> markFor(Player p) {
        if (p == playerOut) return Optional.of(new PlayerMark(MarkType.SUB_OUT, getMinute()));
        if (p == playerIn) return Optional.of(new PlayerMark(MarkType.SUB_IN, getMinute()));
        return Optional.empty();
    }
}