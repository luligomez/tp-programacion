package service;

import model.team.Team;
import model.tournament.zone.Zone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DrawService {

    private static final int POTS = 4;
    private static final int TEAMS_PER_POT = 4;

    public static List<List<Team>> getPots(List<Team> teams) {
        if (teams.size() != POTS * TEAMS_PER_POT) {
            throw new IllegalArgumentException("The draw requires exactly " + POTS * TEAMS_PER_POT + " teams");
        }
        List<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort(Comparator.comparingInt(Team::getRankingPosition));

        List<List<Team>> pots = new ArrayList<>();
        for (int i = 0; i < POTS; i++) {
            int from = i * TEAMS_PER_POT;
            pots.add(new ArrayList<>(sortedTeams.subList(from, from + TEAMS_PER_POT)));
        }
        return pots;
    }

    public static ArrayList<Zone> zoneDraw(List<Team> teams) {
        List<List<Team>> pots = getPots(teams);

        for (List<Team> pot : pots) {
            Collections.shuffle(pot);
        }

        // zone i receives the team i from each pot
        ArrayList<Zone> newZones = new ArrayList<>();
        for (int zoneIndex = 0; zoneIndex < TEAMS_PER_POT; zoneIndex++) {
            Zone zone = new Zone();
            for (List<Team> pot : pots) {
                zone.addTeam(pot.get(zoneIndex));
            }
            newZones.add(zone);
        }
        return newZones;
    }
}