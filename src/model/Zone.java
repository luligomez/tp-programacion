package model;

import java.util.ArrayList;

public class Zone {
    private ArrayList<Team> teams;
    private ArrayList<ZoneMatch> matches;

    public Zone() {
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>(6);
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<ZoneMatch> getMatches() {
        return matches;
    }

    public void addTeam(Team team) {
        if (teams.size() < 4) {
            teams.add(team);
        }
    }

    public void addMatch (ZoneMatch match){
        if (matches.size()<6)
            matches.add(match);
    }
}
