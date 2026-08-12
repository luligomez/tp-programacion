package model;

import java.util.ArrayList;

public class Tournament {
    private ArrayList<Team> teams;
    private Zone[] zones = new Zone[4];

    public Tournament() {
        this.teams = new ArrayList<>();
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public Zone[] getZones() {
        return zones;
    }

    public void addTeam(Team team) {
        if (teams.size() < 16) {
            teams.add(team);
        }
    }
}
