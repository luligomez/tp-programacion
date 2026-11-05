package model.match.zone;

import model.match.Match;
import model.person.Referee;
import model.place.Stadium;
import model.team.Team;
import java.util.List;
import model.tournament.zone.TeamStanding;
import java.io.Serial;
import java.io.Serializable;

public class GroupStageMatch extends Match implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int MATCHDAY;
    // Zone standings right before and right after this match was played (null until it is played)
    private List<TeamStanding> standingsBefore;
    private List<TeamStanding> standingsAfter;

    public GroupStageMatch(Team team1, Team team2, Referee referee, Stadium stadium, int matchday) {
        super(team1, team2, referee, stadium);
        this.MATCHDAY = matchday;
    }

    public int getMatchday() {
        return MATCHDAY;
    }

    public void recordStandings(List<TeamStanding> before, List<TeamStanding> after) {
        if (before == null || after == null) {
            throw new IllegalArgumentException("Standings cannot be null.");
        }
        this.standingsBefore = List.copyOf(before);
        this.standingsAfter = List.copyOf(after);
    }

    public boolean hasStandings() {
        return standingsBefore != null && standingsAfter != null;
    }

    public List<TeamStanding> getStandingsBefore() {
        return standingsBefore == null ? List.of() : standingsBefore;
    }

    public List<TeamStanding> getStandingsAfter() {
        return standingsAfter == null ? List.of() : standingsAfter;
    }

    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) return getTeam1();
        if (getTeam2Goals() > getTeam1Goals()) return getTeam2();
        return null;
    }
}
