package model.match;

import model.place.Stadium;
import model.Team;
import model.person.Referee;

import java.time.LocalDate;

public class FirstLegMatch extends Match {
    private String winningCriteria;


    public FirstLegMatch(LocalDate date, Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium) {
        super(date, team1, team2, referee, team1Formation, team2Formation, stadium);
        this.winningCriteria = null;
    }

    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) {
            this.winningCriteria = "Mayor cantidad de goles";
            return getTeam1();
        }
        if (getTeam2Goals() > getTeam1Goals()) {
            this.winningCriteria = "Mayor cantidad de goles";
            return getTeam2();
        }
        return null;
    }

    public String getWinningCriteria() {
        return winningCriteria;
    }
}
