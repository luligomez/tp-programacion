package service;

import model.reports.playerMinutes.MostMinutesReportItem;
import model.tournament.Tournament;
import model.match.Match;
import model.person.Coach;
import model.person.Position;
import model.person.Referee;
import model.person.player.Player;
import model.person.player.TournamentStats;
import model.team.Team;
import model.reports.player.PlayerReportData;
import model.reports.player.PlayerReportItem;
import model.reports.referee.RefereeReportData;
import model.reports.referee.RefereeReportItem;
import model.reports.team.TeamReportData;
import model.reports.team.TeamReportItem;
import model.reports.scorer.ScorerReportItem;
import model.reports.fairplay.FairPlayReportItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TournamentReportsService {

    public static PlayerReportData generatePlayersReport(Tournament tournament) {
        List<PlayerReportItem> items = new ArrayList<>();

        for (Team team : tournament.getTeams()) {
            if (team.getPlayers() == null) continue;

            for (Player player : team.getPlayers()) {
                int age = player.getAge();
                TournamentStats playerStats = player.getTournamentStats();

                int matchesPlayed = playerStats.getMatchesPlayed();
                int minutesPlayed = playerStats.getMinutesPlayed();
                int goalsScored = playerStats.getGoals();

                int goalsConceded = 0;
                double goalsConcededPerMatch = 0.0;

                if (player.getPosition() == Position.GOALKEEPER) {
                    goalsConceded = playerStats.getGoalsConceded();
                    if (matchesPlayed > 0) {
                        goalsConcededPerMatch = (double) goalsConceded / matchesPlayed;
                    }
                }

                items.add(new PlayerReportItem(
                        player.getName(),
                        team.getName(),
                        player.getPosition(),
                        age,
                        player.getBirthDate(),
                        player.getDocumentType(),
                        player.getDocumentNumber(),
                        player.getRating(),
                        matchesPlayed,
                        minutesPlayed,
                        goalsScored,
                        goalsConceded,
                        goalsConcededPerMatch,
                        playerStats.getStandaloneYellowCards(),
                        playerStats.getDoubleYellowExpulsions(),
                        playerStats.getDirectRedCards()
                ));
            }
        }

        items.sort((p1, p2) -> p1.getPlayerName().compareToIgnoreCase(p2.getPlayerName()));
        return new PlayerReportData(items);
    }

    public static TeamReportData generateTeamsReportData(Tournament tournament) {
        List<TeamReportItem> items = new ArrayList<>();
        List<Match> matches = tournament.getAllMatches();

        for (Team team : tournament.getTeams()) {
            TeamResults results = resultsOf(team, matches);

            items.add(new TeamReportItem(
                    team.getName(),
                    averagePlayerAge(team),
                    coachAge(team),
                    coachNationality(team),
                    results.goalsFor(),
                    results.goalsAgainst(),
                    effectiveness(results)
            ));
        }

        // Sort alphabetically by team name
        items.sort((t1, t2) -> t1.getTeamName().compareToIgnoreCase(t2.getTeamName()));

        return new TeamReportData(items);
    }

    // Goals, matches played and points obtained by a team in the matches already played
    private record TeamResults(int matchesPlayed, int goalsFor, int goalsAgainst, int points) {}

    private static TeamResults resultsOf(Team team, List<Match> matches) {
        int matchesPlayed = 0;
        int goalsFor = 0;
        int goalsAgainst = 0;
        int points = 0;

        for (Match match : matches) {
            if (match == null || !match.isPlayed()) continue;

            boolean isTeam1 = match.getTeam1().equals(team);
            boolean isTeam2 = match.getTeam2().equals(team);
            if (!isTeam1 && !isTeam2) continue;

            // Goals from the point of view of this team, depending on which side it played
            int scored = isTeam1 ? match.getTeam1Goals() : match.getTeam2Goals();
            int conceded = isTeam1 ? match.getTeam2Goals() : match.getTeam1Goals();

            matchesPlayed++;
            goalsFor += scored;
            goalsAgainst += conceded;
            points += pointsOf(scored, conceded);
        }
        return new TeamResults(matchesPlayed, goalsFor, goalsAgainst, points);
    }

    private static double averagePlayerAge(Team team) {
        if (team.getPlayers() == null || team.getPlayers().isEmpty()) return 0.0;

        double totalAge = 0;
        for (Player player : team.getPlayers()) {
            totalAge += player.getAge();
        }
        return totalAge / team.getPlayers().size();
    }

    private static int coachAge(Team team) {
        return team.getCoach() != null ? team.getCoach().getAge() : 0;
    }

    private static String coachNationality(Team team) {
        Coach coach = team.getCoach();
        if (coach == null || coach.getNationality() == null) return "N/A";
        return coach.getNationality().getName();
    }

    // Percentage of points obtained over points available
    private static double effectiveness(TeamResults results) {
        if (results.matchesPlayed() == 0) return 0.0;
        return (results.points() * 100.0) / (results.matchesPlayed() * 3);
    }

    public static RefereeReportData generateRefereesReportData(Tournament tournament) {
        List<RefereeReportItem> items = new ArrayList<>();
        List<Referee> referees = tournament.getReferees();
        int matchesOfficiated, totalYears=0, yrsExp;
        for(Referee ref : referees){
            yrsExp = ref.getYearsOfExperience();
            totalYears+= yrsExp;
            matchesOfficiated = (int) tournament.getAllMatches().stream().filter(match -> match != null && match.isPlayed() && match.getReferee() != null && match.getReferee().equals(ref)).count();
            items.add(new RefereeReportItem(ref.getName(), ref.getNationality().getName(), matchesOfficiated, yrsExp));
        }
        items.sort((r1, r2) -> Integer.compare(r2.getMatchesOfficiated(), r1.getMatchesOfficiated()));
        double avgYears = referees.isEmpty() ? 0.0 : (double) totalYears /referees.size();
        return new RefereeReportData(items,avgYears);
    }

    // SCORER RANKING

    public static List<ScorerReportItem> generateScorersRanking(Tournament tournament) {

        List<ScorerReportItem> scorers = new ArrayList<>();

        for (Team team : tournament.getTeams()) {

            if (team.getPlayers() == null) {
                continue;
            }

            for (Player player : team.getPlayers()) {

                TournamentStats stats = player.getTournamentStats();

                if (stats != null && stats.getGoals() > 0) {
                    scorers.add(new ScorerReportItem(
                            player.getName(),
                            team.getName(),
                            stats.getGoals(),
                            stats.getPenaltyGoals()
                    ));
                }
            }
        }

        scorers.sort((a, b) -> {
            int result = Integer.compare(b.getGoals(), a.getGoals());

            if (result != 0) {
                return result;
            }

            return a.getPlayerName().compareToIgnoreCase(b.getPlayerName());
        });

        return scorers;
    }
    // FAIR PLAY RANKING

    public static List<FairPlayReportItem> generateFairPlayRanking(Tournament tournament) {

        List<FairPlayReportItem> ranking = new ArrayList<>();

        for (Team team : tournament.getTeams()) {

            int yellowCards = 0;
            int doubleYellowExpulsions = 0;
            int directRedCards = 0;

            if (team.getPlayers() == null) {
                continue;
            }

            for (Player player : team.getPlayers()) {

                TournamentStats stats = player.getTournamentStats();

                if (stats != null) {
                    yellowCards += stats.getStandaloneYellowCards();
                    doubleYellowExpulsions += stats.getDoubleYellowExpulsions();
                    directRedCards += stats.getDirectRedCards();
                }
            }

            ranking.add(new FairPlayReportItem(
                    team.getName(),
                    yellowCards,
                    doubleYellowExpulsions,
                    directRedCards
            ));
        }

        // Sort teams by penalty points, lowest first
        ranking.sort((a, b) -> {
            int result = Integer.compare(
                    a.getPenaltyPoints(),
                    b.getPenaltyPoints()
            );

            if (result != 0) {
                return result;
            }

            return a.getTeamName().compareToIgnoreCase(b.getTeamName());
        });

        return ranking;
    }

    private static int pointsOf(int goalsFor, int goalsAgainst) {
        if (goalsFor > goalsAgainst) {
            return 3;
        }
        if (goalsFor == goalsAgainst) {
            return 1;
        }
        return 0;
    }

    public static List<MostMinutesReportItem> getMostMinutesRanking(Tournament tournament) {
        List<MostMinutesReportItem> result = new ArrayList<>();

        for (Team team : tournament.getTeams()) {
            for (Player player : team.getPlayers()) {
                TournamentStats playerStats = player.getTournamentStats();

                int matchesPlayed = playerStats.getMatchesPlayed();
                int minutesPlayed = playerStats.getMinutesPlayed();

                if (minutesPlayed > 0) {
                    result.add(new MostMinutesReportItem(
                            player.getName(),
                            team.getName(),
                            matchesPlayed,
                            minutesPlayed
                    ));
                }
            }
        }

        result.sort(Comparator.comparing(MostMinutesReportItem::getMinutesPlayed,Comparator.reverseOrder())
                .thenComparing(MostMinutesReportItem::getMatchesPlayed,Comparator.reverseOrder())
                .thenComparing(MostMinutesReportItem::getPlayerName)
        );

        return result;
    }

}