package model;

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

        // 1. Filtrar los jugadores habilitados (excluyendo a los suspendidos)
        List<Player> available = new ArrayList<>(team.getPlayers().stream()
                .filter(p -> {
                    if (p.getTournamentStats().isSuspended()) {
                        p.getTournamentStats().setSuspended(false); // Cumple la fecha de sanción
                        return false;
                    }
                    return true;
                })
                .toList());

        // 2. Elegir una táctica al azar para este partido
        // 0 -> 4-3-3 | 1 -> 4-4-2 | 2 -> 5-3-2
        int tactic = random.nextInt(3);
        int defNeeded = (tactic == 2) ? 5 : 4;
        int midNeeded = (tactic == 1) ? 4 : 3;
        int fwdNeeded = (tactic == 0) ? 3 : 2;

        // 3. Seleccionar obligado 1 Arquero
        selectBestForPosition(available, formation, Position.GOALKEEPER, 1, random);

        // 4. Seleccionar los jugadores de campo según la táctica elegida
        selectBestForPosition(available, formation, Position.DEFENDER, defNeeded, random);
        selectBestForPosition(available, formation, Position.MIDFIELDER, midNeeded, random);
        selectBestForPosition(available, formation, Position.FORWARD, fwdNeeded, random);

        // 5. Plan de Respaldo: Si faltaron jugadores en alguna posición específica para llegar a 11 titulares,
        // completamos con los mejores disponibles restantes de cualquier posición
        while (formation.getStarters().size() < Formation.STARTERS_PER_TEAM && !available.isEmpty()) {
            Player bestRemaining = getBestByForm(available, random);
            formation.addStarter(bestRemaining);
            available.remove(bestRemaining);
        }

        // 6. El resto de los convocados disponibles van a la banca de suplentes
        for (Player substitute : available) {
            formation.addSubstitutes(substitute);
        }

        return formation;
    }

    // elegir cant de jugadores de una posición específica según la forma del día
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

    // Calcula la "forma del día" con ±10% de variación aleatoria
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