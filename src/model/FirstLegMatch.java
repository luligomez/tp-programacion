package model;

import java.time.LocalDate;

public class FirstLegMatch extends Match{
    public FirstLegMatch (LocalDate date, Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Fotmation){
        super(date,team1, team2, referee,team1Formation,team2Fotmation);
    }
}
