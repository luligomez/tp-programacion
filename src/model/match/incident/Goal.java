package model.match.incident;

import model.person.player.Player;

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

    public void setScorer(Player scorer) {
        this.scorer = scorer;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }

    public boolean isOwnGoal() {
        return ownGoal;
    }

    public void setOwnGoal(boolean ownGoal) {
        this.ownGoal = ownGoal;
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
}
