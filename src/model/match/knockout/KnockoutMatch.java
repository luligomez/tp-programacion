package model.match.knockout;

import model.Team;
import model.match.Formation;
import model.match.Match;
import model.person.Referee;
import model.place.Stadium;

public abstract class KnockoutMatch extends Match {
    private PenaltyShootout penalties;
    private String winningCriteria;

    public KnockoutMatch(Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium) {
        super(team1, team2, referee, team1Formation, team2Formation, stadium);
    }

    public KnockoutMatch(Team team1, Team team2, Referee referee, Stadium stadium) {
        super(team1, team2, referee, stadium);
    }

    public PenaltyShootout getPenalties() {
        return penalties;
    }

    public void setPenalties(PenaltyShootout penaltyShootout) {
        this.penalties = penaltyShootout;
    }

    public void setWinningCriteria(String winningCriteria) {
        this.winningCriteria = winningCriteria;
    }

    public boolean hasPenalties(){
        return penalties!=null;
    }

    public String getWinningCriteria() {
        return winningCriteria;
    }

    public int getPenaltiesScored(Team team) {
        return penalties == null ? 0 : penalties.getGoals(team);
    }

}
