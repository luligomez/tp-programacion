package model;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Match {

    private LocalDate date;
    private Team team1;
    private Team team2;
    private Referee referee;
    private ArrayList<Incident> incidents;
    private int team1Goals;
    private int team2Goals;
    private Formation team1Formation;
    private Formation team2Formation;
    private Stadium stadium;

    public Match(LocalDate date, Team team1, Team team2, Referee referee,
                 Formation team1Formation, Formation team2Formation, Stadium stadium) {

        this.date = date;
        this.team1 = team1;
        this.team2 = team2;
        this.referee = referee;
        this.incidents = new ArrayList<>();
        this.team1Goals = 0;
        this.team2Goals = 0;
        this.team1Formation = team1Formation;
        this.team2Formation = team2Formation;
        this.stadium = stadium;
    }

    public LocalDate getDate() {
        return date;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public Referee getReferee() {
        return referee;
    }

    public ArrayList<Incident> getIncidents() {
        return incidents;
    }

    public void addIncident(Incident incident) {
        incidents.add(incident);
    }

    public int getTeam1Goals() {
        return team1Goals;
    }

    public int getTeam2Goals() {
        return team2Goals;
    }

    public void setResult(int team1Goals, int team2Goals) {
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }

    public Formation getTeam1Formation() {
        return team1Formation;
    }

    public Formation getTeam2Formation() {
        return team2Formation;
    }

    public abstract Team getWinner();
}