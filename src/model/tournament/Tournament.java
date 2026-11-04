package model.tournament;

import model.team.Team;
import model.match.Match;
import model.tournament.knockout.KnockoutPhase;
import model.tournament.knockout.KnockoutStage;
import model.person.Referee;
import model.place.Stadium;
import model.tournament.zone.ZoneStage;
import model.tournament.zone.Zone;
import model.exception.InvalidTournamentException;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tournament implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;   // cambió la estructura: los guardados viejos ya no cargan

    private static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;
    private static final int KNOCKOUT_MATCHES = 13; // 4 quarter ties x 2 legs + 2 semi ties x 2 legs + the final, one stadium each

    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<Referee> referees = new ArrayList<>();
    private transient ArrayList<Stadium> stadiums = new ArrayList<>(); // se recargan de la BD
    private final ZoneStage zoneStage = new ZoneStage();
    private final KnockoutStage knockoutStage = new KnockoutStage();
    private TournamentState state = TournamentState.NOT_DRAWN;

    // ---------- datos ----------

    public TournamentState getState() { return state; }
    public ArrayList<Team> getTeams() { return teams; }
    public ArrayList<Referee> getReferees() { return referees; }
    public ArrayList<Stadium> getStadiums() { return stadiums; }
    public void setStadiums(ArrayList<Stadium> stadiums) { this.stadiums = stadiums; }
    public ZoneStage getZoneStage() { return zoneStage; }
    public KnockoutStage getKnockoutStage() { return knockoutStage; }

    // los que usa medio proyecto siguen disponibles, delegando
    public ArrayList<Zone> getZones() { return zoneStage.getZones(); }
    public int getCurrentMatchday() { return zoneStage.getCurrentMatchday(); }

    public void addTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }

        if (teams.size() >= TEAMS) {
            throw new InvalidTournamentException(
                    "A tournament cannot have more than " + TEAMS + " teams."
            );
        }

        teams.add(team);
    }
    public void addReferee(Referee referee) {
        if (referee == null) {
            throw new IllegalArgumentException("Referee cannot be null.");
        }
        referees.add(referee);
    }

    // ---------- draw ----------

    public void zoneDraw() {
        zoneStage.draw(teams);
        state = TournamentState.DRAWN_UNCONFIRMED;
    }

    public void resetDraw() { zoneDraw(); }

    public void confirmDraw() {
        long availableStadiums = stadiums == null ? 0 : stadiums.stream().filter(s -> !s.isUsed()).count();
        if (availableStadiums < KNOCKOUT_MATCHES) {
            throw new InvalidTournamentException(
                    "At least " + KNOCKOUT_MATCHES + " stadiums are required to start the tournament, but only "
                            + availableStadiums + " are available. Add more in Cities & Stadiums.");
        }
        state = TournamentState.GROUP_STAGE;
        zoneStage.generateMatches(referees, stadiums, LocalDate.now());
    }

    // ---------- paso de una fase a la otra (acá está la coordinación) ----------

    public void simulateCurrentMatchday() {
        if (zoneStage.isFinished()) return;
        zoneStage.simulateCurrentMatchday();
        if (zoneStage.isFinished()) startKnockoutStage();
    }

    private void startKnockoutStage() {
        state = TournamentState.KNOCKOUT_STAGE;
        knockoutStage.generateQuarterFinals(zoneStage.getZones(), referees, stadiums, getLastMatchDate());
    }

    public void advanceKnockoutIfDecided(KnockoutPhase phase) {
        if (!knockoutStage.hasUnresolvedTies(phase)) {
            knockoutStage.generateNextPhase(phase, referees, stadiums, getLastMatchDate());
        }
    }


    public List<Match> getAllMatches() {
        List<Match> all = new ArrayList<>(zoneStage.getMatches());
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