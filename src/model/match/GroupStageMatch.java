package model.match;

import model.*;
import model.person.Referee;
import model.place.Stadium;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class GroupStageMatch extends Match implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int MATCHDAY;

    public GroupStageMatch(LocalDateTime date, Team team1, Team team2, Referee referee, Stadium stadium, int matchday) {
        super(date, team1, team2, referee, stadium);
        this.MATCHDAY = matchday;
    }

    public int getMATCHDAY() {
        return MATCHDAY;
    }

    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) return getTeam1();
        if (getTeam2Goals() > getTeam1Goals()) return getTeam2();
        return null;
    }
}
