package model.match;

import model.*;
import model.person.Referee;
import model.place.Stadium;
import model.zone.Zone;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class GroupStageMatch extends Match implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Zone zone;

    public GroupStageMatch(LocalDate date, Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation, Stadium stadium, Zone zone) {
        super(date, team1, team2, referee, team1Formation, team2Formation, stadium);
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    public Team getWinner() {
        if (getTeam1Goals() > getTeam2Goals()) return getTeam1();
        if (getTeam2Goals() > getTeam1Goals()) return getTeam2();
        return null;
    }
}
