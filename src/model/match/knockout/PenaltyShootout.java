package model.match.knockout;

import model.person.player.Player;
import model.Team;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PenaltyShootout implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Team team1;
    private final Team team2;
    private int team1Goals = 0;
    private int team2Goals = 0;

    private final List<PenaltyKick> kicks = new ArrayList<>();

    public PenaltyShootout(Team team1, Team team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    public void addKick(Team team, Player kicker, boolean scored) {
        kicks.add(new PenaltyKick(team, kicker, scored));
        if (scored) {
            if (team.equals(team1)) {
                team1Goals++;
            } else {
                team2Goals++;
            }
        }
    }

    public Team getWinner() {
        if (team1Goals > team2Goals) return team1;
        if (team2Goals > team1Goals) return team2;
        return null; // Aún en definición o no iniciada
    }

    public int getTeam1Goals() { return team1Goals; }
    public int getTeam2Goals() { return team2Goals; }
    public List<PenaltyKick> getKicks() { return kicks; }

    // Clase interna o separada para representar cada tiro individual de la tanda
    public static class PenaltyKick implements Serializable {
        private final Team team;
        private final Player kicker;
        private final boolean scored;

        public PenaltyKick(Team team, Player kicker, boolean scored) {
            this.team = team;
            this.kicker = kicker;
            this.scored = scored;
        }

        public Team getTeam() { return team; }
        public Player getKicker() { return kicker; }
        public boolean isScored() { return scored; }
    }
}
