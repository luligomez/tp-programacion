package model;

import java.time.LocalDate;

public class GroupStageMatch extends Match {

    public GroupStageMatch(LocalDate date, Team team1, Team team2, Referee referee, Formation team1Formation, Formation team2Formation){
        super(date, team1, team2, referee, team1Formation, team2Formation);
    }
}
