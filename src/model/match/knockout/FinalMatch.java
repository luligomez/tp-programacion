package model.match.knockout;

import model.match.Formation;
import model.place.Stadium;
import model.Team;
import model.person.Referee;

import java.time.LocalDateTime;

public class FinalMatch extends KnockoutMatch {

    public FinalMatch(LocalDateTime date, Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium) {
        super(date, team1, team2, referee, team1Formation, team2Formation, stadium);
    }


    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) {
            super.setWinningCriteria("Higher number of goals in 90 minutes");
            return getTeam1();
        }
        if (getTeam2Goals() > getTeam1Goals()) {
            super.setWinningCriteria("Higher number of goals in 90 minutes");
            return getTeam2();
        }
        //se define por penales
        super.setWinningCriteria("Won on penalties");
        PenaltyShootout penaltyShootout = super.getPenalties();
        return penaltyShootout == null ? null : penaltyShootout.getWinner();
    }

}
