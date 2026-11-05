package model.match.incident;

import java.io.Serial;
import java.io.Serializable;

import model.match.Match;
import model.person.player.Player;
import model.reports.match.MatchReport.PlayerMark;
import model.reports.match.MatchReport.TimelineEvent;
import java.util.Optional;

public abstract class Incident implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int minute;

    public Incident(int minute) {
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String toString(){
        return "Min " + minute + "'";
    }

    // How this incident appears on the timeline (empty if not shown)
    public abstract Optional<TimelineEvent> toTimelineEvent(Match match);

    // Which symbol this player gets for this incident (empty if not involved)
    public Optional<PlayerMark> markFor(Player player) {
        return Optional.empty();
    }

    public void registerStats() {}
}
