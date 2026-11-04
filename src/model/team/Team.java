package model.team;

import model.person.Coach;
import model.person.player.Player;
import model.place.Country;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import model.person.Position;

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

        if (players == null || players.size() != 18) {
            throw new IllegalArgumentException(
                    "A team must have exactly 18 players."
            );
        }

        long goalkeepers = players.stream()
                .filter(p -> p.getPosition() == Position.GOALKEEPER)
                .count();

        long defenders = players.stream()
                .filter(p -> p.getPosition() == Position.DEFENDER)
                .count();

        long midfielders = players.stream()
                .filter(p -> p.getPosition() == Position.MIDFIELDER)
                .count();

        long forwards = players.stream()
                .filter(p -> p.getPosition() == Position.FORWARD)
                .count();

        if (goalkeepers != 2 || defenders != 6 ||
                midfielders != 5 || forwards != 5) {
            throw new IllegalArgumentException(
                    "Invalid team composition. Required: 2 goalkeepers, " +
                            "6 defenders, 5 midfielders and 5 forwards."
            );
        }

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
