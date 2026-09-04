package model;

import model.person.Coach;
import model.person.player.Player;
import model.place.Country;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Team implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Country country;
    private int rankingPosition;
    private List<Player> players;
    private Coach coach;

    public Team(String name, Country country, int rankingPosition,
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Coach getCoach() {
        return coach;
    }

}
