package model;

public class Goal extends Incident {

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
}