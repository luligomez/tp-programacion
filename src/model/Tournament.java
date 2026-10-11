package model;

import model.match.*;
import model.match.knockout.*;
import model.person.Referee;
import model.place.Stadium;
import model.zone.Zone;
import service.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static service.MatchSimulator.*;
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

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public ArrayList<Zone> getZones() {
        return zones;
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

    public void zoneDraw() {
        // 1. Delegamos el sorteo al servicio
        this.zones = DrawService.zoneDraw(this.teams);

        // 2. Actualizamos el estado interno del torneo
        this.state = TournamentState.DRAWN_UNCONFIRMED;
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
        FixtureScheduler.scheduleZoneStage(zones, LocalDate.now());
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


    public void confirmDraw() {
        state = GROUP_STAGE;
        this.generateGroupStageMatches();
    }

    public void resetDraw() {
        zoneDraw();
    }

    public ArrayList<Team> getKnockoutWinners(KnockoutPhase phase) {

        return knockoutTies.stream()
                .filter(tie -> tie.getPhase() == phase)
                .map(KnockoutTie::getWinner)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void generateQuarterFinals() {
        this.state = TournamentState.KNOCKOUT_STAGE;
        this.knockoutTies.clear();
        // 1. Delegamos la lógica de los cruces al servicio
        List<KnockoutTie> qfTies = KnockoutService.generateQuarterFinals(this.zones, this.referees, this.stadiums);
        this.knockoutTies.addAll(qfTies);
        // 2. Programamos las fechas (Tournament coordina los tiempos)
        FixtureScheduler.scheduleQuarterFinals(this.getKnockoutTies(KnockoutPhase.QUARTER_FINAL), this.getLastMatchDate());
    }

    public void generateSemiFinals() {
        ArrayList<Team> semifinalists = getKnockoutWinners(KnockoutPhase.QUARTER_FINAL);
        // 1. Delegamos al servicio
        List<KnockoutTie> semiTies = KnockoutService.generateSemiFinals(semifinalists, this.referees, this.stadiums);
        this.knockoutTies.addAll(semiTies);
        // 2. Programamos las fechas
        FixtureScheduler.scheduleSemiFinals(this.getKnockoutTies(KnockoutPhase.SEMI_FINAL), this.getLastMatchDate());
    }

    public void generateFinal() {
        ArrayList<Team> finalists = getKnockoutWinners(KnockoutPhase.SEMI_FINAL);
        // 1. Delegamos al servicio
        this.finalMatch = KnockoutService.generateFinal(finalists, this.referees, this.stadiums);
        // 2. Programamos la fecha
        FixtureScheduler.scheduleFinal(this.finalMatch, this.getLastMatchDate());
    }

    public void simulateFinal() {
        MatchSimulator.simulateFinal(finalMatch);
    }
    public void resolveFinal(){
        simulatePenaltyShootout(finalMatch);
    }

    public int getCurrentMatchday() {
        return currentMatchday;
    }

    public ArrayList<KnockoutTie> getKnockoutTies() {
        return knockoutTies;
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

    public void resolveTies(KnockoutPhase phase) {
        knockoutTies.stream()
                .filter(tie -> tie.getPhase() == phase && tie.getWinner() == null)
                .forEach(tie -> simulatePenaltyShootout(tie.getSecondLeg()));
    }

    public boolean hasFinal() {
        return finalMatch != null;
    }

    public boolean isFinalPlayed() {
        return finalMatch != null && finalMatch.isPlayed();
    }

    public boolean hasUnresolvedTies(KnockoutPhase phase){
        return knockoutTies.stream()
                .anyMatch(tie -> phase == tie.getPhase() && tie.getWinner() == null);

    }

    public boolean hasUnresolvedFinal(){
        return finalMatch.getWinner()==null;
    }


        public LocalDate getLastMatchDate() {
        List<Match> matches = getAllMatches();
        if (matches == null) {
            return LocalDate.now(); // O una fecha por defecto si la lista no existe
        }

        return matches.stream()
                .filter(Objects::nonNull)                // 1. Filtra partidos nulos PRIMERO
                .map(Match::getDateTime)                 // 2. Obtiene la fecha/hora
                .filter(Objects::nonNull)                // 3. Filtra fechas/horas nulas (sin programar)
                .map(LocalDateTime::toLocalDate)         // 4. Convierte a LocalDate
                .max(LocalDate::compareTo)               // 5. Busca la fecha más lejana
                .orElse(LocalDate.now());
    }


}


