package model.tournament.zone;

import model.match.zone.GroupStageMatch;
import java.util.List;

public class MatchStandingComparison {

    private final GroupStageMatch match;
    private final List<TeamStanding> standingsBefore;
    private final List<TeamStanding> standingsAfter;

    public MatchStandingComparison(
            GroupStageMatch match,
            List<TeamStanding> standingsBefore,
            List<TeamStanding> standingsAfter) {

        this.match = match;
        this.standingsBefore = List.copyOf(standingsBefore);
        this.standingsAfter = List.copyOf(standingsAfter);
    }

    public GroupStageMatch getMatch() {
        return match;
    }

    public List<TeamStanding> getStandingsBefore() {
        return standingsBefore;
    }

    public List<TeamStanding> getStandingsAfter() {
        return standingsAfter;
    }
}