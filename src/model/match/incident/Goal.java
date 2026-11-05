package model.match.incident;

import model.match.Match;
import model.person.player.Player;
import model.reports.match.MatchReport.*;
import java.util.Optional;

import java.io.Serial;

public class Goal extends Incident {

    @Serial
    private static final long serialVersionUID = 1L;
    private Player scorer;
    private boolean penalty;
    private boolean ownGoal;
    private Player goalkeeper;

    public Goal(int minute, Player scorer, boolean penalty,
                boolean ownGoal, Player goalkeeper) {

        super(minute);

        this.scorer = scorer;
        this.penalty = penalty;
        this.ownGoal = ownGoal;
        this.goalkeeper = goalkeeper;
    }

    public Player getScorer() {
        return scorer;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public boolean isOwnGoal() {
        return ownGoal;
    }

    public Player getGoalkeeper() {
        return goalkeeper;
    }

    public void setGoalkeeper(Player goalkeeper) {
        this.goalkeeper = goalkeeper;
    }

    @Override
    public String toString() {
        String detail = ownGoal ? " (Own Goal)" : penalty ? " (Penalty)" : "";
        String author = (scorer != null) ? scorer.getName() : "Unknown";
        String gk = (goalkeeper != null) ? " (GK: " + goalkeeper.getName() + ")" : "";
        return super.toString()+" | ⚽ GOAL! " + author + detail + gk;
    }

    @Override
    public Optional<TimelineEvent> toTimelineEvent(Match match) {
        // an own goal counts for the author's opposing team
        Side side = (match.isTeam1Player(scorer) != ownGoal) ? Side.TEAM1 : Side.TEAM2;
        EventType type = ownGoal ? EventType.OWN_GOAL
                : penalty ? EventType.PENALTY_GOAL : EventType.GOAL;
        return Optional.of(new TimelineEvent(getMinute(), side, type, scorer.getName()));
    }

    @Override
    public Optional<PlayerMark> markFor(Player player) {
        if (player != scorer) return Optional.empty();
        return Optional.of(new PlayerMark(ownGoal ? MarkType.OWN_GOAL : MarkType.GOAL, getMinute()));
    }

    @Override
    public void registerStats() {
        // an own goal does not count for its author
        if (!ownGoal && scorer != null) {
            scorer.getTournamentStats().registerGoal(penalty);
        }
        // the goalkeeper concedes the goal even if it was an own goal
        if (goalkeeper != null) {
            goalkeeper.getTournamentStats().registerGoalConceded();
        }
    }
}
