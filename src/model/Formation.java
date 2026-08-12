package model;

import java.util.ArrayList;

public class Formation {

    private ArrayList<Player> players;

    public Formation() {
        this.players = new ArrayList<>();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (players.size() < 11) {
            players.add(player);
        }
    }
}