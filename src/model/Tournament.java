package model;

import java.util.ArrayList;

public class Tournament {
    public static final int GROUPS = 4;
    public static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;
    public static final int QUALIFIED_PER_GROUP = 2;
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<Zone> zones = new ArrayList<>();

    public Tournament(){}

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void addTeam(Team team) {
        if (teams.size() < 16) {
            teams.add(team);
        }
    }
}
