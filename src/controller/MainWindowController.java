package controller;

import DAO.CityDAO;
import DAO.StadiumDAO;
import controller.admin.CitiesController;
import controller.admin.StadiumsController;
import controller.rankings.FairPlayRankingController;
import controller.rankings.MostMinutesRankingController;
import controller.rankings.ScorerRankingController;
import model.place.Stadium;
import service.FileReader;
import model.tournament.Tournament;
import model.tournament.TournamentState;
import model.match.Match;
import service.TournamentRepository;
import view.admin.AdminView;
import view.mainwindow.MainWindowView;
import view.mainwindow.SidebarItem;
import view.rankings.RankingsView;
import service.CredentialPdfService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import org.json.simple.parser.ParseException;
import org.openpdf.text.DocumentException;

import static model.tournament.TournamentState.*;

public class MainWindowController {

    private MainWindowView view;
    private Tournament tournament;
    private TournamentRepository repository;

    public MainWindowController(MainWindowView view, Tournament tournament, TournamentRepository repository) {
        this.view = view;
        this.tournament = tournament;
        this.repository = repository;
        initController();
    }

    private void initController() {
        view.setOnSidebarSelectListener(this::onSidebarItemSelected);
        view.setOnResetListener(this::onResetTournament);
        navigateToGroups();
    }

    private void onSidebarItemSelected(SidebarItem item) {
        switch (item) {
            case GROUPS -> navigateToGroups();
            case KNOCKOUT -> navigateToKnockout();
            case TEAMS -> navigateToTeams();
            case PLAYERS -> navigateToPlayers();
            case REFEREES -> navigateToReferees();
            case ADMIN -> navigateToAdmin();
            case RANKINGS -> navigateToRankings();
            case CREDENTIALS -> generateCredentials();
        }
    }

    private void navigateToGroups() {
        ZoneStageController zoneStageController = new ZoneStageController(
                tournament,
                match -> showMatchDetail(match, this::navigateToGroups, SidebarItem.GROUPS),
                this::saveTournament
        );
        view.showScreen(zoneStageController.getView(), SidebarItem.GROUPS);
    }

    private void onResetTournament() {
        boolean confirmed = view.showConfirm(
                "Are you sure? The current tournament will be lost.",
                "Reset tournament"
        );

        if (confirmed) {
            try {
                this.tournament = FileReader.fileReader("torneo.json");
                repository.delete();
                this.tournament.setStadiums(new StadiumDAO().getAllStadiums());
                navigateToGroups();

                view.showInfo("Tournament successfully reset!", "Reset Complete");
            } catch (IOException | ParseException | SQLException e) {
                view.showError("Error loading tournament file: " + e.getMessage(), "Error Resetting Tournament");
            }
        }
    }

    private void navigateToKnockout() {
        TournamentState state = tournament.getState();
        if (state == NOT_DRAWN || state == DRAWN_UNCONFIRMED || state == GROUP_STAGE) {
            view.showWarning(
                    "You must complete the group stage before accessing the knockout stage.",
                    "Group stage not finished"
            );
            return;
        }
        KnockoutController knockoutController = new KnockoutController(
                tournament,
                match -> showMatchDetail(match, this::navigateToKnockout, SidebarItem.KNOCKOUT),
                this::saveTournament
        );
        view.showScreen(knockoutController.getView(), SidebarItem.KNOCKOUT);
    }

    private void navigateToTeams() {
        TeamsController teamsController = new TeamsController(tournament);
        view.showScreen(teamsController.getView(), SidebarItem.TEAMS);
    }

    private void navigateToPlayers() {
        PlayersController playersController = new PlayersController(tournament);
        view.showScreen(playersController.getView(), SidebarItem.PLAYERS);
    }

    private void navigateToReferees() {
        RefereesController refereesController = new RefereesController(tournament);
        view.showScreen(refereesController.getView(), SidebarItem.REFEREES);
    }

    private void navigateToRankings() {
        ScorerRankingController scorerRankingController = new ScorerRankingController(tournament);
        FairPlayRankingController fairPlayRankingController = new FairPlayRankingController(tournament);
        MostMinutesRankingController mostMinutesRankingController = new MostMinutesRankingController(tournament);

        RankingsView rankingsView = new RankingsView(
                scorerRankingController.getView(),
                fairPlayRankingController.getView(),
                mostMinutesRankingController.getView()

        );

        view.showScreen(rankingsView, SidebarItem.RANKINGS);

    }

    private void navigateToAdmin() {
        CitiesController citiesController = new CitiesController(new CityDAO());
        StadiumsController stadiumsController = new StadiumsController(
                new StadiumDAO(),
                new CityDAO(),
                this::refreshTournamentStadiums
        );

        AdminView adminView = new AdminView(
                citiesController.getView(),
                stadiumsController.getView(),
                stadiumsController::loadCities
        );

        view.showScreen(adminView, SidebarItem.ADMIN);
    }

    private void refreshTournamentStadiums(List<Stadium> stadiums) {
        tournament.setStadiums(new ArrayList<>(stadiums));
        tournament.syncUsedStadiums();
    }

    private void showMatchDetail(Match match, Runnable onBack, SidebarItem item) {
        MatchDetailController matchDetailController = new MatchDetailController(match, onBack);
        view.showScreen(matchDetailController.getView(), item);
    }

    private void generateCredentials() {
        File destinationFile = view.chooseSaveFile("credentials.pdf", "Save credentials PDF");
        if (destinationFile == null) {
            return;
        }
        try {
            new CredentialPdfService().generate(tournament, destinationFile.toPath());
            view.showInfo("Credentials PDF created successfully.", "Success");
        } catch (IOException | DocumentException e) {
            view.showError("Could not create credentials: " + e.getMessage(), "Error");
        }
    }

    private void saveTournament() {
        try {
            repository.save(tournament);
        } catch (IOException e) {
            view.showWarning("Could not save progress: " + e.getMessage(), "Save error");
        }
    }
}
