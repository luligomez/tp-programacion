package model.match;

import model.exception.InvalidFormationException;
import model.person.Position;
import model.person.player.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Formation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final int STARTERS_PER_TEAM = 11;
    private ArrayList<Player> starters = new ArrayList<>();
    private ArrayList<Player> substitutes = new ArrayList<>();

    public Formation() {
    }

    public ArrayList<Player> getStarters() {
        return starters;
    }

    public void addStarter(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        if (starters.size() >= STARTERS_PER_TEAM) {
            throw new InvalidFormationException(
                    "A formation cannot have more than 11 starters."
            );
        }

        starters.add(player);
    }

    public ArrayList<Player> getSubstitutes() {
        return substitutes;
    }


    public void addSubstitutes(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        if (starters.contains(player) || substitutes.contains(player)) {
            throw new InvalidFormationException(
                    "The player " + player.getName() + " is already in this formation."
            );
        }

        substitutes.add(player);
    }

    public Player getGoalkeeper() {
        for (Player p : starters) {
            if (p.getPosition() == Position.GOALKEEPER) {
                return p;
            }
        }
        throw new InvalidFormationException("The Formation has no assigned goalkeeper among the starters.");
    }
}