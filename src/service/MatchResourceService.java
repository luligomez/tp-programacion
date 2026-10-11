package service;

import model.Team;
import model.person.Referee;
import model.place.Stadium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatchResourceService {
    public static Stadium pickRandomUnusedStadium(ArrayList<Stadium> stadiums) {
        List<Stadium> available = stadiums.stream()
                .filter(s -> !s.isUsed())
                .toList();
        if (available.isEmpty()) {
            throw new IllegalStateException("No unused stadiums available");
        }
        Stadium chosen = available.get(new Random().nextInt(available.size()));
        chosen.setUsed(true);
        return chosen;
    }

    public static Stadium pickRandomStadium(ArrayList<Stadium> stadiums) {
        List<Stadium> available = stadiums.stream()
                .toList();
        if (available.isEmpty()) {
            throw new IllegalStateException("No unused stadiums available");
        }
        return available.get(new Random().nextInt(available.size()));
    }

    public static Referee pickValidReferee(Team team1, Team team2, ArrayList<Referee> referees) {
        List<Referee> valid = referees.stream()
                .filter(r -> team1.getCountry().equals(team2.getCountry())
                        || (!r.getNationality().equals(team1.getCountry())
                        && !r.getNationality().equals(team2.getCountry())))
                .toList();
        if (valid.isEmpty()) {
            throw new IllegalStateException("No valido referee for this match");
        }
        return valid.get(new Random().nextInt(valid.size()));
    }
}
