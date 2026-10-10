package model.match.incident;

import model.match.Match;
import model.person.player.Player;
import model.reports.MatchReport.*;

import java.io.Serial;
import java.util.Optional;

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

    @Override
    public String toString() {
        String pName = (player != null) ? player.getName() : "Player";
        String type = doubleYellow ? " (2nd Yellow)" : " (Direct Red)";
        return super.toString()+ " | 🟥 Red Card: " + pName + type;
    }

    @Override
    public Optional<TimelineEvent> toTimelineEvent(Match match) {
        Side side = match.isTeam1Player(player) ? Side.TEAM1 : Side.TEAM2;
        EventType type = doubleYellow ? EventType.SECOND_YELLOW_RED : EventType.RED_CARD;
        return Optional.of(new TimelineEvent(getMinute(), side, type, player.getName(), null));
    }

    @Override
    public Optional<PlayerMark> markFor(Player p) {
        return p == player ? Optional.of(new PlayerMark(MarkType.RED, getMinute())) : Optional.empty();
    }
}