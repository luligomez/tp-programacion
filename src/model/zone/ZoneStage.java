package model.zone;

import model.Team;
import model.match.Match;
import model.person.Referee;
import model.place.Stadium;
import service.DrawService;
import service.FixtureScheduler;
import service.FormationCreator;
import service.MatchSimulator;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ZoneStage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int MATCHDAYS = 3;

    private ArrayList<Zone> zones = new ArrayList<>();
    private int currentMatchday = 1;

    public ArrayList<Zone> getZones() { return zones; }

    public int getCurrentMatchday() { return currentMatchday; }

    public boolean isFinished() { return currentMatchday > MATCHDAYS; }

    public void draw(ArrayList<Team> teams) {
        zones = DrawService.zoneDraw(teams);
    }

    public void generateMatches(ArrayList<Referee> referees, ArrayList<Stadium> stadiums, LocalDate start) {
        for (Zone zone : zones) {
            zone.generateMatches(referees, stadiums);
        }
        assignFormationsForMatchday(1);
        FixtureScheduler.scheduleZoneStage(zones, start);
    }

    public void simulateCurrentMatchday() {
        if (isFinished()) return;

        for (Zone zone : zones) {
            zone.getGroupStageMatches().stream()
                    .filter(m -> m.getMATCHDAY() == currentMatchday && !m.isPlayed())
                    .forEach(m -> {
                        MatchSimulator.simulateMatch(m);
                        zone.registerMatchResult(m);
                    });
        }

        currentMatchday++;
        if (!isFinished()) {
            assignFormationsForMatchday(currentMatchday);
        }
    }

    public List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        zones.forEach(zone -> matches.addAll(zone.getGroupStageMatches()));
        return matches;
    }

    private void assignFormationsForMatchday(int matchday) {
        for (Zone zone : zones) {
            zone.getGroupStageMatches().stream()
                    .filter(m -> m.getMATCHDAY() == matchday)
                    .forEach(m -> {
                        m.setTeam1Formation(FormationCreator.createAutomaticFormation(m.getTeam1()));
                        m.setTeam2Formation(FormationCreator.createAutomaticFormation(m.getTeam2()));
                    });
        }
    }
}