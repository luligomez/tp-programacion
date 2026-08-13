package model;

import java.time.LocalDate;

public class SecondLegMatch extends Match{
    private boolean penalties;
    private int team1PenaltyGoals;
    private int team2PenaltyGoals;
    private String winningCriteria;

    public SecondLegMatch(LocalDate date, Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium) {
        super(date, team1, team2, referee, team1Formation, team2Formation, stadium);
        this.penalties = false;
        this.winningCriteria = null;
    }

    public void executePenalties(int team1Penalties, int team2Penalties) {
        this.penalties = true;
        this.team1PenaltyGoals = team1Penalties;
        this.team2PenaltyGoals = team2Penalties;
    }

    public boolean hasPenalties(){
        return penalties;
    }

    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) {
            this.winningCriteria = "Mayor cantidad de goles en 90 minutos";
            return getTeam1();
        }
        if (getTeam2Goals() > getTeam1Goals()) {
            this.winningCriteria = "Mayor cantidad de goles en 90 minutos";
            return getTeam2();
        }
        //se define por penales
        this.winningCriteria = "Ganador en tiros penales";
        return team1PenaltyGoals > team2PenaltyGoals ? getTeam1() : getTeam2();
    }

    public String getWinningCriteria() {
        return winningCriteria;
    }
}
