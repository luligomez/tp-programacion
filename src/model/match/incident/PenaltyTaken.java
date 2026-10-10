package model.match.incident;

import model.match.Match;
import model.person.player.Player;
import model.reports.MatchReport.*;

import java.io.Serial;
import java.util.Optional;

public class PenaltyTaken extends Incident {

    @Serial
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

    @Override
    public String toString() {
        String shooter = (player != null) ? player.getName() : "Unknown";
        String scoredStr = (scored) ? "Penalty Scored!" : "Penalty Missed!";
        return super.toString()+" | 🥅 "+scoredStr+" Shooter: " + shooter;
    }

    @Override
    public Optional<TimelineEvent> toTimelineEvent(Match match) {
        return Optional.empty();
    }
}