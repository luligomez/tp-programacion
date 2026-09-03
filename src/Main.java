
import model.Team;
import model.Tournament;
import model.person.Referee;
import model.person.player.Player;

import static model.FileReader.fileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Tournament tournament = fileReader("torneo.json");
        for(Team t : tournament.getTeams()) {
            System.out.println(t.getName() +" "+ t.getRankingPosition());
            for (Player p : t.getPlayers()) {
                System.out.println(p.getRating());
            }
        }
        for (Referee r : tournament.getReferees()) {
            System.out.println(r.getName());
        }

    }


}