package model.match.knockout;

import model.FormationCreator;
import model.Team;
import model.Tournament;
import model.match.Formation;
import model.person.Referee;
import model.place.Stadium;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class KnockoutTie implements Serializable {
    private KnockoutPhase phase;
    private Team team1;
    private Team team2;
    private FirstLegMatch firstLeg;
    private SecondLegMatch secondLeg;

    public KnockoutTie(KnockoutPhase phase, Team team1, Team team2, ArrayList<Referee> referees, ArrayList<Stadium> stadiums) {
        this.phase = phase;
        this.team1 = team1;
        this.team2 = team2;
        initTie(referees, stadiums);
    }

    private void initTie(ArrayList<Referee> referees, ArrayList<Stadium> stadiums){
        // La serie se encarga de instanciar la ida y la vuelta con localías invertidas
        Referee ref1 = Tournament.pickValidReferee(team1, team2, referees);
        Stadium st1 = Tournament.pickRandomUnusedStadium(stadiums);
        Formation formation1 = FormationCreator.createAutomaticFormation(team1);
        Formation formation2 = FormationCreator.createAutomaticFormation(team2);
        this.firstLeg = new FirstLegMatch(LocalDate.now(), team1, team2, ref1, formation1, formation2, st1);

        Referee ref2 = Tournament.pickValidReferee(team2, team1, referees);
        Stadium st2 = Tournament.pickRandomUnusedStadium(stadiums);
        this.secondLeg = new SecondLegMatch(LocalDate.now(), team2, team1, ref2, null, null, st2); //TODO CAMBIAR HORARIO
    }

    public KnockoutPhase getPhase() { return phase; }
    public Team getTeam1() { return team1; }
    public Team getTeam2() { return team2; }
    public FirstLegMatch getFirstLeg() { return firstLeg; }
    public SecondLegMatch getSecondLeg() { return secondLeg; }


    public boolean isResolved() {
        return secondLeg != null && secondLeg.isPlayed();
    }

    public Team getWinner() {
        if (!isResolved()) {
            return null;
        }

        // 1. Goles globales acumulados
        // team1 es local en la ida (firstLeg) y visitante en la vuelta (secondLeg)
        // team2 es visitante en la ida (firstLeg) y local en la vuelta (secondLeg)
        int goalsTeam1 = firstLeg.getTeam1Goals() + secondLeg.getTeam2Goals();
        int goalsTeam2 = firstLeg.getTeam2Goals() + secondLeg.getTeam1Goals();

        if (goalsTeam1 != goalsTeam2) {
            return goalsTeam1 > goalsTeam2 ? team1 : team2;
        }

        // 2. Gol de visitante
        int awayGoalsTeam1 = secondLeg.getTeam2Goals(); // team1 fue visitante en la vuelta
        int awayGoalsTeam2 = firstLeg.getTeam2Goals();  // team2 fue visitante en la ida

        if (awayGoalsTeam1 != awayGoalsTeam2) {
            return awayGoalsTeam1 > awayGoalsTeam2 ? team1 : team2;
        }

        // 3. Si hay empate global absoluto, consultar el ganador de los penales
        if (secondLeg.getPenalties() != null) {
            return secondLeg.getPenalties().getWinner();
        }

        return null;
    }

    public boolean needsPenaltyShootout() {
        if (!isResolved())
            return false;
        int goalsTeam1 = firstLeg.getTeam1Goals() + secondLeg.getTeam2Goals();
        int goalsTeam2 = firstLeg.getTeam2Goals() + secondLeg.getTeam1Goals();
        int awayGoalsTeam1 = secondLeg.getTeam2Goals();
        int awayGoalsTeam2 = firstLeg.getTeam2Goals();
        return (goalsTeam1 == goalsTeam2) && (awayGoalsTeam1 == awayGoalsTeam2);
    }

}