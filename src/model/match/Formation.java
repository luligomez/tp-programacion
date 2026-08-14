package model.match;

import model.person.Player;

import java.util.ArrayList;

public class Formation {
    public static int STARTERS_PER_TEAM = 11;
    private ArrayList<Player> starters = new ArrayList<>();
    private ArrayList<Player> substitutes = new ArrayList<>();

    public Formation() {
    }

    public ArrayList<Player> getStarters() {
        return starters;
    }

    public void addStarter(Player player) {
        if (starters.size() < STARTERS_PER_TEAM) {
            starters.add(player);
        }
    }

    public ArrayList<Player> getSubstitutes() {
        return substitutes;
    }

    public void addSubstitutes(Player player) {
            substitutes.add(player);
    }
}