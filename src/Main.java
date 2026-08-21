
import model.Team;
import model.Tournament;
import model.person.player.Player;

import static model.FileReader.fileReader;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Tournament tournament = fileReader("torneo.json");
        for(Team t : tournament.getTeams()) {
            System.out.println(t.getName() +" "+ t.getRankingPosition());
            for (Player p : t.getPlayers()) {
                System.out.println(p.getRating());
            }
        }

    }


}