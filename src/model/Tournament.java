package model;

import model.match.Match;
import model.match.knockout.KnockoutPhase;
import model.match.knockout.KnockoutStage;
import model.person.Referee;
import model.place.Stadium;
import model.zone.ZoneStage;
import model.zone.Zone;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tournament implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;   // cambió la estructura: los guardados viejos ya no cargan

    private static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;

    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<Referee> referees = new ArrayList<>();
    private transient ArrayList<Stadium> stadiums = new ArrayList<>(); // se recargan de la BD
    private final ZoneStage ZoneStage = new ZoneStage();
    private final KnockoutStage knockoutStage = new KnockoutStage();
    private TournamentState state = TournamentState.NOT_DRAWN;

    // ---------- datos ----------

    public TournamentState getState() { return state; }
    public ArrayList<Team> getTeams() { return teams; }
    public ArrayList<Referee> getReferees() { return referees; }
    public ArrayList<Stadium> getStadiums() { return stadiums; }
    public void setStadiums(ArrayList<Stadium> stadiums) { this.stadiums = stadiums; }
    public ZoneStage getZoneStage() { return ZoneStage; }
    public KnockoutStage getKnockoutStage() { return knockoutStage; }

    // los que usa medio proyecto siguen disponibles, delegando
    public ArrayList<Zone> getZones() { return ZoneStage.getZones(); }
    public int getCurrentMatchday() { return ZoneStage.getCurrentMatchday(); }

    public void addTeam(Team team) {
        if (teams.size() < TEAMS) teams.add(team);
    }
    public void addReferee(Referee referee) { referees.add(referee); }
    public void addStadium(Stadium stadium) { stadiums.add(stadium); }
    public void addAllStadiums(ArrayList<Stadium> stadia) { stadiums.addAll(stadia); }

    // ---------- sorteo ----------

    public void zoneDraw() {
        ZoneStage.draw(teams);
        state = TournamentState.DRAWN_UNCONFIRMED;
    }

    public void resetDraw() { zoneDraw(); }

    public void confirmDraw() {
        state = TournamentState.GROUP_STAGE;
        ZoneStage.generateMatches(referees, stadiums, LocalDate.now());
    }

    // ---------- paso de una fase a la otra (acá está la coordinación) ----------

    public void simulateCurrentMatchday() {
        if (ZoneStage.isFinished()) return;
        ZoneStage.simulateCurrentMatchday();
        if (ZoneStage.isFinished()) startKnockoutStage();
    }

    private void startKnockoutStage() {
        state = TournamentState.KNOCKOUT_STAGE;
        knockoutStage.generateQuarterFinals(ZoneStage.getZones(), referees, stadiums, getLastMatchDate());
    }

    public void advanceKnockoutIfDecided(KnockoutPhase phase) {
        if (!knockoutStage.hasUnresolvedTies(phase)) {
            knockoutStage.generateNextPhase(phase, referees, stadiums, getLastMatchDate());
        }
    }

    // ---------- consultas sobre todo el torneo ----------

    public List<Match> getAllMatches() {
        List<Match> all = new ArrayList<>(ZoneStage.getMatches());
        all.addAll(knockoutStage.getMatches());
        return all;
    }

    public LocalDate getLastMatchDate() {
        return getAllMatches().stream()
                .map(Match::getDateTime)
                .filter(Objects::nonNull)
                .map(LocalDateTime::toLocalDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    // Marca como usados los estadios de las eliminatorias, tras recargarlos de la BD
    public void syncUsedStadiums() {
        knockoutStage.getMatches().stream()
                .map(Match::getStadium)
                .filter(Objects::nonNull)
                .forEach(used -> stadiums.stream()
                        .filter(s -> s.getName().equals(used.getName()))
                        .findFirst()
                        .ifPresent(s -> s.setUsed(true)));
    }
}