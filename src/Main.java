
import model.Tournament;

import static model.FileReader.fileReader;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Tournament tournament = fileReader("torneo.json");
        System.out.println(tournament.getTeams().size());
        System.out.println(tournament.getTeams().getFirst().getName());

    }


}