package model;

import java.util.List;

public class Team {
    private Country country;
    private String name;
    private int rankingPosition;
    private List<Player> players;
    private Coach coach;

    public Team(Country country, String name, int rankingPosition,
                List<Player> players, Coach coach) {

        this.country = country;
        this.name = name;
        this.rankingPosition = rankingPosition;
        this.players = players;
        this.coach = coach;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

    public void setRankingPosition(int rankingPosition) {
        this.rankingPosition = rankingPosition;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }
}
