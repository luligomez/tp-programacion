package model;

import model.match.*;
import model.person.Referee;
import model.place.Stadium;
import model.zone.Zone;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import static model.TournamentState.GROUP_STAGE;

public class Tournament implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int GROUPS = 4;
    private static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;
    private static final int QUALIFIED_PER_GROUP = 2;
    private ArrayList<Team> teams = new ArrayList<>();
    private ArrayList<Zone> zones = new ArrayList<>();
    private ArrayList<Match> matches = new ArrayList<>();
    private ArrayList<FirstLegMatch> quarterFinalMatches = new ArrayList<>();
    private ArrayList<SecondLegMatch> quarterFinalSecondLegMatches = new ArrayList<>();
    private ArrayList<FirstLegMatch> semiFinalMatches = new ArrayList<>();
    private ArrayList<SecondLegMatch> semiFinalSecondLegMatches = new ArrayList<>();
    private ArrayList<Referee> referees = new ArrayList<>();
    private transient ArrayList<Stadium> stadiums = new ArrayList<>(); //no se guardan los estadios, se consultan nuevamente de la bbdd
    private TournamentState state = TournamentState.NOT_DRAWN;
    private int currentMatchday = 1; // Controla la fecha actual (1, 2 o 3)


    public Tournament() {
    }

    public TournamentState getState() {
        return state;
    }

    public void setState(TournamentState state) {
        this.state = state;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public ArrayList<FirstLegMatch> getQuarterFinalMatches() {
        return quarterFinalMatches;
    }

    public ArrayList<FirstLegMatch> getSemiFinalMatches() {
        return semiFinalMatches;
    }

    public ArrayList<SecondLegMatch> getSemiFinalSecondLegMatches() {
        return semiFinalSecondLegMatches;
    }


    public void generateSemiFinals() {

        ArrayList<Team> winners = getQuarterFinalWinners();

        Referee referee = referees.get(0);
        Stadium stadium = stadiums.isEmpty() ? null : stadiums.get(0);


        FirstLegMatch semi1 = new FirstLegMatch(
                java.time.LocalDate.now(),
                winners.get(0),
                winners.get(1),
                referee,
                null,
                null,
                stadium
        );


        FirstLegMatch semi2 = new FirstLegMatch(
                java.time.LocalDate.now(),
                winners.get(2),
                winners.get(3),
                referee,
                null,
                null,
                stadium
        );


        matches.add(semi1);
        matches.add(semi2);
        semiFinalMatches.add(semi1);
        semiFinalMatches.add(semi2);
    }

    public void generateSemiFinalSecondLegs() {

        ArrayList<SecondLegMatch> secondLegs = new ArrayList<>();

        for (FirstLegMatch match : semiFinalMatches) {

            SecondLegMatch secondLeg = new SecondLegMatch(
                    java.time.LocalDate.now(),
                    match.getTeam2(),
                    match.getTeam1(),
                    referees.get(0),
                    null,
                    null,
                    stadiums.isEmpty() ? null : stadiums.get(0)
            );

            secondLegs.add(secondLeg);
            semiFinalSecondLegMatches.add(secondLeg);
        }

        matches.addAll(secondLegs);
    }

    public ArrayList<Referee> getReferees() {
        return referees;
    }

    public ArrayList<Stadium> getStadiums() {
        return stadiums;
    }

    public void addZone(Zone zone) {
        if (zones.size() < GROUPS) {
            zones.add(zone);
        }
    }

    public void addTeam(Team team) {
        if (teams.size() < TEAMS) {
            teams.add(team);
        }
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void addReferee(Referee referee) {
        referees.add(referee);
    }

    public void addStadium(Stadium stadium) {
        stadiums.add(stadium);
    }

    public void addAllStadiums(ArrayList<Stadium> stadia) {
        this.stadiums.addAll(stadia);
    }

    public List<List<Team>> getPots() {
        List<Team> sortedTeams = new ArrayList<>(this.teams);
        sortedTeams.sort(Comparator.comparingInt(Team::getRankingPosition));

        List<List<Team>> pots = new ArrayList<>();
        pots.add(new ArrayList<>(sortedTeams.subList(0, 4)));
        pots.add(new ArrayList<>(sortedTeams.subList(4, 8)));
        pots.add(new ArrayList<>(sortedTeams.subList(8, 12)));
        pots.add(new ArrayList<>(sortedTeams.subList(12, 16)));
        return pots;
    }

    public void zoneDraw() {
        this.zones.clear();

        List<List<Team>> pots = getPots();

        // mezclamos cada bombo
        for (List<Team> pot : pots) {
            Collections.shuffle(pot);
        }

        // 5. Repartimos un equipo de cada bombo a cada zona
        for (int i = 0; i < 4; i++) {
            this.zones.add(new Zone());
            Zone zonaActual = this.zones.get(i);
            zonaActual.addTeam(pots.get(0).get(i));
            zonaActual.addTeam(pots.get(1).get(i));
            zonaActual.addTeam(pots.get(2).get(i));
            zonaActual.addTeam(pots.get(3).get(i));
        }
        state = TournamentState.DRAWN_UNCONFIRMED;
    }

    public void generateGroupStageMatches() {
        for (Zone zone : zones) {
            zone.generateMatches(referees, stadiums);
        }
    }

    public void syncUsedStadiums() { //actualizamos el booleano used al abrir el programa, luego de cargar los estadios desde la bbdd.
        this.matches.stream()
                // Filtramos los partidos que NO son de fase de grupos
                .filter(match -> !(match instanceof GroupStageMatch))

                // transformamos el flujo de "Partidos" a un flujo de "Estadios"
                .map(Match::getStadium)

                // descartamos los nulos
                .filter(Objects::nonNull)

                // 4. Por cada estadio usado encontrado, lo buscamos en la lista viva
                .forEach(usedStadium -> {
                    this.stadiums.stream()
                            .filter(stadium -> stadium.getName().equals(usedStadium.getName()))
                            .findFirst()
                            .ifPresent(stadium -> stadium.setUsed(true));
                });
    }


    public boolean hasZonesDrawn() {
        return state != TournamentState.NOT_DRAWN;
    }

    public boolean isDrawConfirmed() {
        return state == TournamentState.GROUP_STAGE;
    }

    public void confirmDraw() {
        state = GROUP_STAGE;
        this.generateGroupStageMatches();
    }

    public void resetDraw() {
        zoneDraw();
    }

    public ArrayList<Team> getQualifiedTeams() {

        ArrayList<Team> qualifiedTeams = new ArrayList<>();

        for (Zone zone : zones) {
            qualifiedTeams.addAll(zone.getQualifiedTeams());
        }

        return qualifiedTeams;
    }

    public void generateQuarterFinals() {

        ArrayList<Team> qualifiedTeams = getQualifiedTeams();

        // acá después asignaremos árbitro y estadio correctamente
        Referee referee = referees.get(0);
        Stadium stadium = stadiums.isEmpty() ? null : stadiums.get(0);


        FirstLegMatch quarter1 = new FirstLegMatch(
                java.time.LocalDate.now(),
                qualifiedTeams.get(0),
                qualifiedTeams.get(3),
                referee,
                null,
                null,
                stadium
        );


        FirstLegMatch quarter2 = new FirstLegMatch(
                java.time.LocalDate.now(),
                qualifiedTeams.get(2),
                qualifiedTeams.get(1),
                referee,
                null,
                null,
                stadium
        );


        FirstLegMatch quarter3 = new FirstLegMatch(
                java.time.LocalDate.now(),
                qualifiedTeams.get(4),
                qualifiedTeams.get(7),
                referee,
                null,
                null,
                stadium
        );


        FirstLegMatch quarter4 = new FirstLegMatch(
                java.time.LocalDate.now(),
                qualifiedTeams.get(6),
                qualifiedTeams.get(5),
                referee,
                null,
                null,
                stadium
        );


        matches.add(quarter1);
        matches.add(quarter2);
        matches.add(quarter3);
        matches.add(quarter4);

        quarterFinalMatches.add(quarter1);
        quarterFinalMatches.add(quarter2);
        quarterFinalMatches.add(quarter3);
        quarterFinalMatches.add(quarter4);
    }

    public void generateQuarterFinalSecondLegs() {

        for (FirstLegMatch match : quarterFinalMatches) {

            SecondLegMatch secondLeg = new SecondLegMatch(
                    java.time.LocalDate.now(),
                    match.getTeam2(),
                    match.getTeam1(),
                    referees.get(0),
                    null,
                    null,
                    stadiums.isEmpty() ? null : stadiums.get(0)
            );

            matches.add(secondLeg);
            quarterFinalSecondLegMatches.add(secondLeg);
        }
    }

    public ArrayList<SecondLegMatch> getQuarterFinalSecondLegMatches() {

        return quarterFinalSecondLegMatches;

    }

    public ArrayList<Team> getQuarterFinalWinners() {

        ArrayList<Team> winners = new ArrayList<>();

        for (int i = 0; i < quarterFinalMatches.size(); i++) {

            FirstLegMatch firstLeg = quarterFinalMatches.get(i);
            SecondLegMatch secondLeg = quarterFinalSecondLegMatches.get(i);

            int team1Goals =
                    firstLeg.getTeam1Goals() + secondLeg.getTeam2Goals();

            int team2Goals =
                    firstLeg.getTeam2Goals() + secondLeg.getTeam1Goals();


            if (team1Goals > team2Goals) {

                winners.add(firstLeg.getTeam1());

            } else if (team2Goals > team1Goals) {

                winners.add(firstLeg.getTeam2());

            } else {

                // empate global
                // por ahora usamos ganador de la vuelta
                winners.add(secondLeg.getWinner());
            }
        }

        return winners;
    }

    public ArrayList<Team> getSemiFinalWinners() {

        ArrayList<Team> winners = new ArrayList<>();

        for (int i = 0; i < semiFinalMatches.size(); i++) {

            FirstLegMatch firstLeg = semiFinalMatches.get(i);
            SecondLegMatch secondLeg = semiFinalSecondLegMatches.get(i);

            int team1Goals =
                    firstLeg.getTeam1Goals() + secondLeg.getTeam2Goals();

            int team2Goals =
                    firstLeg.getTeam2Goals() + secondLeg.getTeam1Goals();


            if (team1Goals > team2Goals) {

                winners.add(firstLeg.getTeam1());

            } else if (team2Goals > team1Goals) {

                winners.add(firstLeg.getTeam2());

            } else {

                // si empatan en el global, por ahora usamos penales
                winners.add(secondLeg.getWinner());

            }
        }

        return winners;
    }

    public void generateFinal() {

        ArrayList<Team> finalists = getSemiFinalWinners();

        Referee referee = referees.get(0);
        Stadium stadium = stadiums.isEmpty() ? null : stadiums.get(0);


        FinalMatch finalMatch = new FinalMatch(
                java.time.LocalDate.now(),
                finalists.get(0),
                finalists.get(1),
                referee,
                null,
                null,
                stadium
        );


        matches.add(finalMatch);
    }
    public int getCurrentMatchday() {
        return currentMatchday;
    }

    public void setCurrentMatchday ( int currentMatchday){
        this.currentMatchday = currentMatchday;
    }
}
