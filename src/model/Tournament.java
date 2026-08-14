package model;

import model.match.Match;
import model.zone.Zone;

import java.util.ArrayList;

public class Tournament {
    public static final int GROUPS = 4;
    public static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;
    public static final int QUALIFIED_PER_GROUP = 2;
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<Zone> zones = new ArrayList<>();
    private ArrayList<Match> matches = new ArrayList<>();


    public Tournament(){}

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void addZone(Zone zone) {
        if (zones.size() < GROUPS) {
            zones.add(zone);
        }
    }

    public void addTeam(Team team) {
        if (teams.size() < TEAMS) {
            teams.add(team);
        }
    }

    public void addMatch(Match match) {
        matches.add(match);
    }
}
