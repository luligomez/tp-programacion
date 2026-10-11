package service;

import model.Team;
import model.match.knockout.*;
import model.match.Formation;
import model.tournament.knockout.KnockoutPhase;
import model.match.knockout.KnockoutTie;
import model.person.Referee;
import model.place.Stadium;
import model.zone.Zone;

import java.util.ArrayList;
import java.util.List;

public class KnockoutService {

    // 1. Generar Cuartos de Final
    public static List<KnockoutTie> generateQuarterFinals(List<Zone> zones, ArrayList<Referee> referees, ArrayList<Stadium> stadiums) {
        List<KnockoutTie> qfTies = new ArrayList<>();

        // 1° y 2° de cada Zona
        Team firstA = zones.get(0).getQualifiedTeams().get(0);
        Team secondA = zones.get(0).getQualifiedTeams().get(1);
        Team firstB = zones.get(1).getQualifiedTeams().get(0);
        Team secondB = zones.get(1).getQualifiedTeams().get(1);
        Team firstC = zones.get(2).getQualifiedTeams().get(0);
        Team secondC = zones.get(2).getQualifiedTeams().get(1);
        Team firstD = zones.get(3).getQualifiedTeams().get(0);
        Team secondD = zones.get(3).getQualifiedTeams().get(1);

        // Cruces
        qfTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstA, secondD, referees, stadiums)); // I
        qfTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstB, secondC, referees, stadiums)); // II
        qfTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstC, secondA, referees, stadiums)); // III
        qfTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstD, secondB, referees, stadiums)); // IV

        return qfTies;
    }

    // 2. Generar Semifinales
    public static List<KnockoutTie> generateSemiFinals(List<Team> quarterWinners, ArrayList<Referee> referees, ArrayList<Stadium> stadiums) {
        List<KnockoutTie> semiTies = new ArrayList<>();

        if (quarterWinners.size() == 4) {
            Team winnerI = quarterWinners.get(0);
            Team winnerII = quarterWinners.get(1);
            Team winnerIII = quarterWinners.get(2);
            Team winnerIV = quarterWinners.get(3);

            semiTies.add(new KnockoutTie(KnockoutPhase.SEMI_FINAL, winnerI, winnerII, referees, stadiums)); // V
            semiTies.add(new KnockoutTie(KnockoutPhase.SEMI_FINAL, winnerIII, winnerIV, referees, stadiums)); // VI
        }
        return semiTies;
    }

    // 3. Generar la Final
    public static FinalMatch generateFinal(List<Team> semiWinners, ArrayList<Referee> referees, ArrayList<Stadium> stadiums) {
        if (semiWinners.size() != 2) {
            throw new IllegalStateException("2 semifinal winners are needed to generate the final");
        }

        Team team1 = semiWinners.get(0);
        Team team2 = semiWinners.get(1);

        Formation f1 = FormationCreator.createAutomaticFormation(team1);
        Formation f2 = FormationCreator.createAutomaticFormation(team2);

        Referee ref = MatchResourceService.pickValidReferee(team1, team2, referees);
        Stadium stadium = MatchResourceService.pickRandomUnusedStadium(stadiums);

        return new FinalMatch(team1, team2, ref, f1, f2, stadium);
    }
}