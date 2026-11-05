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
import java.util.Collections;

public class Tournament implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;   // structure changed: old saved files can no longer be loaded

    private static final int TEAMS = 16;
    public static final int TEAMS_PER_GROUP = 4;
    private static final int KNOCKOUT_MATCHES = 13; // 4 quarter ties x 2 legs + 2 semi ties x 2 legs + the final, one stadium each

    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<Referee> referees = new ArrayList<>();
    private transient ArrayList<Stadium> stadiums = new ArrayList<>(); // reloaded from the DB
    private final ZoneStage zoneStage = new ZoneStage();
    private final KnockoutStage knockoutStage = new KnockoutStage();
    private TournamentState state = TournamentState.NOT_DRAWN;

    // ---------- data ----------

    public TournamentState getState() { return state; }
    public List<Team> getTeams() { return Collections.unmodifiableList(teams); }
    public List<Referee> getReferees() { return Collections.unmodifiableList(referees); }
    public ArrayList<Stadium> getStadiums() { return stadiums; }
    public void setStadiums(ArrayList<Stadium> stadiums) { this.stadiums = stadiums; }
    public ZoneStage getZoneStage() { return zoneStage; }
    public KnockoutStage getKnockoutStage() { return knockoutStage; }

    // the ones used across the project remain available, delegating
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

    // ---------- moving from one stage to the next (coordination happens here) ----------
    // Checks, before anything is played, that the phases still to be generated will find enough unused stadiums
    public void requireKnockoutStadiums() {
        int needed = 0;
        if (knockoutStage.getTies(KnockoutPhase.QUARTER_FINAL).isEmpty()) needed += 8;   // 4 ties x 2 legs
        if (knockoutStage.getTies(KnockoutPhase.SEMI_FINAL).isEmpty()) needed += 4;      // 2 ties x 2 legs
        if (!knockoutStage.hasFinal()) needed += 1;
        long available = stadiums == null ? 0 : stadiums.stream().filter(s -> !s.isUsed()).count();
        if (available < needed) {
            throw new InvalidTournamentException(
                    "At least " + needed + " unused stadiums are needed for the rest of the tournament, but only "
                            + available + " are available. Add more in Cities & Stadiums.");
        }
    }

    public void simulateCurrentMatchday() {
        if (zoneStage.isFinished()) return;
        requireKnockoutStadiums();
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

    // Marks the knockout stadiums as used, after reloading them from the DB
    public void syncUsedStadiums() {
        knockoutStage.getMatches().stream()
                .map(Match::getStadium)
                .filter(Objects::nonNull)
                .forEach(used -> stadiums.stream()
                        .filter(s -> s.getId() == used.getId())
                        .findFirst()
                        .ifPresent(s -> s.setUsed(true)));
    }
}