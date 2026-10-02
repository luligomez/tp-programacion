package model;

import model.match.*;
import model.match.knockout.FirstLegMatch;
import model.match.knockout.KnockoutPhase;
import model.match.knockout.KnockoutTie;
import model.match.knockout.SecondLegMatch;
import model.person.Referee;
import model.place.Stadium;
import model.zone.Zone;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static model.MatchSimulator.*;
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
    private ArrayList<KnockoutTie> knockoutTies = new ArrayList<>();
    private FinalMatch finalMatch;
    private ArrayList<Referee> referees = new ArrayList<>();
    private transient ArrayList<Stadium> stadiums = new ArrayList<>(); //no se guardan los estadios, se consultan nuevamente de la bbdd
    private TournamentState state = TournamentState.NOT_DRAWN;
    private int currentMatchday = 1; // Controla la fecha actual (1, 2 o 3)


    public Tournament() {
    }

    public void setStadiums(ArrayList<Stadium> stadiums) {
        this.stadiums = stadiums;
    }

    public FinalMatch getFinalMatch() {
        return finalMatch;
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

    /*
    public ArrayList<FirstLegMatch> getQuarterFinalMatches() {
        return quarterFinalMatches;
    }

    public ArrayList<FirstLegMatch> getSemiFinalMatches() {
        return semiFinalMatches;
    }

    public ArrayList<SecondLegMatch> getSemiFinalSecondLegMatches() {
        return semiFinalSecondLegMatches;
    }
*/

    public void generateSemiFinals() {

        ArrayList<Team> semifinalists = getKnockoutWinners(KnockoutPhase.QUARTER_FINAL);
        Team winnerI = semifinalists.get(0);
        Team winnerII= semifinalists.get(1);
        Team winnerIII = semifinalists.get(2);
        Team winnerIV = semifinalists.get(3);

        knockoutTies.add(new KnockoutTie(KnockoutPhase.SEMI_FINAL, winnerI, winnerII, referees, stadiums)); //V
        knockoutTies.add(new KnockoutTie(KnockoutPhase.SEMI_FINAL, winnerIII, winnerIV, referees, stadiums)); // VI

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
            Zone currentZone = this.zones.get(i);
            currentZone.addTeam(pots.get(0).get(i));
            currentZone.addTeam(pots.get(1).get(i));
            currentZone.addTeam(pots.get(2).get(i));
            currentZone.addTeam(pots.get(3).get(i));
        }
        state = TournamentState.DRAWN_UNCONFIRMED;
    }

    public void generateGroupStageMatches() {
        for (Zone zone : zones) {
            zone.generateMatches(referees, stadiums);
            for(GroupStageMatch m: zone.getGroupStageMatches()){
                if(m.getMATCHDAY() == 1){
                    // crea formaciones de los equipos (solo fecha 1)
                    m.setTeam1Formation(FormationCreator.createAutomaticFormation(m.getTeam1()));
                    m.setTeam2Formation(FormationCreator.createAutomaticFormation(m.getTeam2()));
                }
            }
        }
    }

    public void syncUsedStadiums() { //actualizamos el booleano used al abrir el programa, luego de cargar los estadios desde la bbdd.
        this.getAllMatches().stream()
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

    public void generateQuarterFinals() {
        this.state = TournamentState.KNOCKOUT_STAGE;
        this.knockoutTies.clear();

        // 1° y 2° de cada Zona
        Team firstA = zones.get(0).getQualifiedTeams().get(0);
        Team secondA = zones.get(0).getQualifiedTeams().get(1);
        Team firstB = zones.get(1).getQualifiedTeams().get(0);
        Team secondB = zones.get(1).getQualifiedTeams().get(1);
        Team firstC = zones.get(2).getQualifiedTeams().get(0);
        Team secondC = zones.get(2).getQualifiedTeams().get(1);
        Team firstD = zones.get(3).getQualifiedTeams().get(0);
        Team secondD = zones.get(3).getQualifiedTeams().get(1);


        // Cruces: 1A vs 2D, 1B vs 2C, 1C vs 2A, 1D vs 2B
        knockoutTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstA, secondD, referees, stadiums)); //I
        knockoutTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstB, secondC, referees, stadiums)); // II
        knockoutTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstC, secondA, referees, stadiums)); // III
        knockoutTies.add(new KnockoutTie(KnockoutPhase.QUARTER_FINAL, firstD, secondB, referees, stadiums)); // IV

    }

    public List<SecondLegMatch> getQuarterFinalSecondLegMatches() {

        return knockoutTies.stream()
                .filter(tie -> tie.getPhase() == KnockoutPhase.QUARTER_FINAL)
                .map(KnockoutTie::getSecondLeg)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ArrayList<Team> getKnockoutWinners(KnockoutPhase phase) {

        return knockoutTies.stream()
                .filter(tie -> tie.getPhase() == phase)
                .map(KnockoutTie::getWinner)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void generateFinal() {

        ArrayList<Team> finalists = getKnockoutWinners(KnockoutPhase.SEMI_FINAL);
        Team team1 = finalists.getFirst();
        Team team2 = finalists.getLast();
        Formation f1 = FormationCreator.createAutomaticFormation(team1);
        Formation f2 = FormationCreator.createAutomaticFormation(team2);
        Referee ref1 = Tournament.pickValidReferee(team1, team2, referees);
        Stadium st1 = Tournament.pickRandomUnusedStadium(stadiums);
        finalMatch = new FinalMatch(LocalDate.now(),team1, team2, ref1, f1, f2, st1);

    }

    public void simulateFinal() {
        MatchSimulator.simulateFinal(finalMatch);
    }

    public int getCurrentMatchday() {
        return currentMatchday;
    }

    public void setCurrentMatchday (int currentMatchday){
        this.currentMatchday = currentMatchday;
    }

    public static Stadium pickRandomUnusedStadium(ArrayList<Stadium> stadiums) {
        List<Stadium> available = stadiums.stream()
                .filter(s -> !s.isUsed())
                .toList();
        if (available.isEmpty()) {
            throw new IllegalStateException("No unused stadiums available");
        }
        Stadium chosen = available.get(new Random().nextInt(available.size()));
        chosen.setUsed(true);
        return chosen;
    }

    public static Stadium pickRandomStadium(ArrayList<Stadium> stadiums) {
        List<Stadium> available = stadiums.stream()
                .toList();
        if (available.isEmpty()) {
            throw new IllegalStateException("No unused stadiums available");
        }
        return available.get(new Random().nextInt(available.size()));
    }

    public ArrayList<KnockoutTie> getKnockoutTies() {
        return knockoutTies;
    }

    public static Referee pickValidReferee(Team team1, Team team2, ArrayList<Referee> referees) {
        List<Referee> valid = referees.stream()
                .filter(r -> team1.getCountry().equals(team2.getCountry())
                        || (!r.getNationality().equals(team1.getCountry())
                        && !r.getNationality().equals(team2.getCountry())))
                .toList();
        if (valid.isEmpty()) {
            throw new IllegalStateException("No valido referee for this match");
        }
        return valid.get(new Random().nextInt(valid.size()));
    }

    public List<Match> getAllMatches() {
        List<Match> allMatches = new ArrayList<>();

        // 1. Partidos de la Fase de Grupos
        for (Zone zone : zones) {
            allMatches.addAll(zone.getGroupStageMatches());
        }

        // 2. Partidos de la Fase Eliminatoria (Playoffs)
        for (KnockoutTie tie : knockoutTies) {
            if (tie.getFirstLeg() != null) {
                allMatches.add(tie.getFirstLeg());
            }
            if (tie.getSecondLeg() != null) {
                    allMatches.add(tie.getSecondLeg());
            }
        }

        allMatches.add(finalMatch);

        return allMatches;
    }
    
    public void assignFormationsForMatchday(int matchday) {
        for (Zone zone : this.zones) {
            zone.getGroupStageMatches().stream()
                    .filter(match -> match.getMATCHDAY() == matchday)
            .forEach(match -> {
                //Crear las formaciones usando el FormationCreator
                Formation f1 = FormationCreator.createAutomaticFormation(match.getTeam1());
                Formation f2 = FormationCreator.createAutomaticFormation(match.getTeam2());
                // Asignarlas al partido
                match.setTeam1Formation(f1);
                match.setTeam2Formation(f2);
            });
        }
    }

    public void simulateCurrentMatchday() {
        if (this.currentMatchday > 3) {
            return;
        }

        // 1. Delegar la simulación de los partidos de la fecha actual
        zones.forEach(zone -> {
            zone.getGroupStageMatches().stream()
                    .filter(match -> match.getMATCHDAY() == currentMatchday && !match.isPlayed())
                    .forEach(match -> {
                        simulateMatch(match);
                        zone.registerMatchResult(match);
                    });
        });

        // 2. Avanzar de fecha
        this.currentMatchday++;

        // 3. Evaluar cambio de estado o preparar la siguiente fecha
        if (this.currentMatchday <= 3) {
            this.assignFormationsForMatchday(this.currentMatchday);
        } else {
            this.state = TournamentState.KNOCKOUT_STAGE;
            this.generateQuarterFinals();
        }
    }

    private void assignFirstLegFormations(KnockoutPhase phase) {
        for (KnockoutTie tie : knockoutTies) {
            if (tie.getPhase() == phase) {
                FirstLegMatch firstLegMatch = tie.getFirstLeg();
                firstLegMatch.setTeam1Formation(FormationCreator.createAutomaticFormation(firstLegMatch.getTeam1()));
                firstLegMatch.setTeam2Formation(FormationCreator.createAutomaticFormation(firstLegMatch.getTeam2()));
            }
        }
    }

    private void assignSecondLegFormations(KnockoutPhase phase) {
        for (KnockoutTie tie : knockoutTies) {
            if (tie.getPhase() == phase) {
                SecondLegMatch secondLeg = tie.getSecondLeg();
                secondLeg.setTeam1Formation(FormationCreator.createAutomaticFormation(secondLeg.getTeam1()));
                secondLeg.setTeam2Formation(FormationCreator.createAutomaticFormation(secondLeg.getTeam2()));
            }
        }
    }

    public boolean isFirstLegPlayed(KnockoutPhase phase) {
        return knockoutTies.stream()
                .filter(tie -> tie.getPhase() == phase)
                .allMatch(tie -> tie.getFirstLeg().isPlayed());
    }

    public boolean isSecondLegPlayed(KnockoutPhase phase) {
        return knockoutTies.stream()
                .filter(tie -> tie.getPhase() == phase)
                .allMatch(tie -> tie.getSecondLeg().isPlayed());
    }

    public void simulateKnockoutFirstLeg(KnockoutPhase phase) {
        // 2. Simular los partidos de ida de la fase
        for (KnockoutTie tie : knockoutTies) {
            if(tie.getPhase() == phase)
                simulateMatch(tie.getFirstLeg());
        }
        this.assignSecondLegFormations(phase);
    }

    public void simulateKnockoutSecondLeg(KnockoutPhase phase) {
        for (KnockoutTie tie : knockoutTies) {
            if(tie.getPhase() == phase) {
                Match match = tie.getSecondLeg();
                simulateMatch(match);
                resolveTieIfNeeded(tie);
            }
        }
    }
    //?????????????????????????? esta bien preguntar si la fase es cuartos o semi?
    public void generateNextKnockoutPhase(KnockoutPhase phase) {
        if (phase == KnockoutPhase.QUARTER_FINAL) {
            generateSemiFinals();
        } else if (phase == KnockoutPhase.SEMI_FINAL) {
            generateFinal();
        }
    }

    public boolean hasKnockoutPhase(KnockoutPhase phase) {
        return knockoutTies.stream().anyMatch(tie -> tie.getPhase() == phase);
    }

    public List<KnockoutTie> getKnockoutTies(KnockoutPhase phase) {
        return knockoutTies.stream()
                .filter(tie -> tie.getPhase() == phase)
                .collect(Collectors.toList());
    }

    public static void resolveTieIfNeeded(KnockoutTie tie) {
        if (tie.needsPenaltyShootout()) {
            simulatePenaltyShootout(tie.getSecondLeg());
        }
    }

    public boolean hasFinal() {
        return finalMatch != null;
    }

    public boolean isFinalPlayed() {
        return finalMatch != null && finalMatch.isPlayed();
    }
}


