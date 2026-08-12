package model;

import java.util.ArrayList;

public class Zone {
    private ArrayList<Team> teams;

    public Zone() {
        this.teams = new ArrayList<>();
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        if (teams.size() < 4) {
            teams.add(team);
        }
    }
}
