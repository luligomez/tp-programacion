package model.match.knockout;

import model.match.Formation;
import model.place.Stadium;
import model.team.Team;
import model.person.Referee;

public class FinalMatch extends KnockoutMatch {

    public FinalMatch(Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium) {
        super(team1, team2, referee, team1Formation, team2Formation, stadium);
    }


    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) return getTeam1();
        if (getTeam2Goals() > getTeam1Goals()) return getTeam2();
        PenaltyShootout penaltyShootout = getPenalties();
        return penaltyShootout == null ? null : penaltyShootout.getWinner();
    }

    public String getWinningCriteria() {
        if (getWinner() == null) return "";
        return getTeam1Goals() != getTeam2Goals() ? "Goals" : "Penalties";
    }

}
