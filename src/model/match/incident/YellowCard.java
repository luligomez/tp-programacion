package model.match.incident;

import model.match.Match;
import model.person.player.Player;
import model.reports.MatchReport.*;

import java.io.Serial;
import java.util.Optional;

public class YellowCard extends Incident {

    @Serial
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

    @Override
    public String toString() {
        String pName = (player != null) ? player.getName() : "Player";
        return super.toString()+ " | 🟨 Yellow Card: " + pName;
    }

    @Override
    public Optional<TimelineEvent> toTimelineEvent(Match match) {
        Side side = match.isTeam1Player(player) ? Side.TEAM1 : Side.TEAM2;
        return Optional.of(new TimelineEvent(getMinute(), side, EventType.YELLOW_CARD, player.getName(), null));
    }

    @Override
    public Optional<PlayerMark> markFor(Player p) {
        return p == player ? Optional.of(new PlayerMark(MarkType.YELLOW, getMinute())) : Optional.empty();
    }
}