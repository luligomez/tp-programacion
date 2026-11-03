package model.match.knockout;

import model.place.Stadium;
import model.team.Team;
import model.person.Referee;

public class SecondLegMatch extends KnockoutMatch {

    public SecondLegMatch(Team team1, Team team2, Referee referee, Stadium stadium) {
        super(team1, team2, referee, stadium);
    }

    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) return getTeam1();
        if (getTeam2Goals() > getTeam1Goals()) return getTeam2();
        PenaltyShootout penaltyShootout = getPenalties();
        return penaltyShootout == null ? null : penaltyShootout.getWinner();
    }


}
