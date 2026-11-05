package service;

import model.team.Team;
import model.match.Formation;
import model.person.Position;
import model.person.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FormationCreator {

    public static Formation createAutomaticFormation(Team team) {
        Formation formation = new Formation();
        Random random = new Random();

        // 1. Filter the available players (excluding suspended ones)
        List<Player> available = new ArrayList<>(team.getPlayers().stream()
                .filter(p -> {
                    if (p.getTournamentStats().isSuspended()) {
                        p.getTournamentStats().setSuspended(false); // Serves the suspension matchday
                        return false;
                    }
                    return true;
                })
                .toList());

        // 2. Pick a random tactic for this match
        // 0 -> 4-3-3 | 1 -> 4-4-2 | 2 -> 5-3-2
        int tactic = random.nextInt(3);
        int defNeeded = (tactic == 2) ? 5 : 4;
        int midNeeded = (tactic == 1) ? 4 : 3;
        int fwdNeeded = (tactic == 0) ? 3 : 2;

        // 3. Seleccionar obligado 1 Arquero
        selectBestForPosition(available, formation, Position.GOALKEEPER, 1, random);

        // 4. Select the field players according to the chosen tactic
        selectBestForPosition(available, formation, Position.DEFENDER, defNeeded, random);
        selectBestForPosition(available, formation, Position.MIDFIELDER, midNeeded, random);
        selectBestForPosition(available, formation, Position.FORWARD, fwdNeeded, random);

        // 5. Fallback plan: if there were not enough players in a specific position to reach 11 starters,
        // fill in with the best remaining available players from any position
        while (formation.getStarters().size() < Formation.STARTERS_PER_TEAM && !available.isEmpty()) {
            Player bestRemaining = getBestByForm(available, random);
            formation.addStarter(bestRemaining);
            available.remove(bestRemaining);
        }

        // 6. The rest of the available squad goes to the substitutes bench
        for (Player substitute : available) {
            formation.addSubstitutes(substitute);
        }

        return formation;
    }
    

    // pick the number of players for a specific position according to the day's form
    private static void selectBestForPosition(List<Player> pool, Formation formation,
                                              Position position, int count, Random random) {
        for (int i = 0; i < count; i++) {
            Player selected = pool.stream()
                    .filter(p -> p.getPosition() == position)
                    .max(Comparator.comparingDouble(p -> getEffectiveRating(p, random)))
                    .orElse(null);

            if (selected != null) {
                formation.addStarter(selected);
                pool.remove(selected);
            }
        }
    }

    // Calculates the "form of the day" with ±10% random variation
    private static double getEffectiveRating(Player player, Random random) {
        double formFactor = 0.90 + (0.20 * random.nextDouble());
        return player.getRating() * formFactor;
    }

    private static Player getBestByForm(List<Player> players, Random random) {
        return players.stream()
                .max(Comparator.comparingDouble(p -> getEffectiveRating(p, random)))
                .orElse(null);
    }
}