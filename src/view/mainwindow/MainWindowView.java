package view.mainwindow;

import model.Tournament;
import view.zonestage.ZoneStageView;

import javax.swing.*;
import java.awt.*;

public class MainWindowView extends JFrame {
    private TopBar topBar;
    private Sidebar sidebar;
    private JPanel contentPanel;
    private Tournament tournament;

    public MainWindowView(Tournament tournament) {
        super("International Clubs Cup");
        this.tournament = tournament;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // centra la ventana en pantalla

        setLayout(new BorderLayout());

        topBar = new TopBar(this::onResetTournament);
        add(topBar, BorderLayout.NORTH);

        sidebar = new Sidebar(this::onSidebarItemSelected);
        add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        showInitialScreen();
    }

    private void showInitialScreen() {
        // acá después conectamos con Tournament para decidir en qué fase arrancar
        showScreen(new ZoneStageView(tournament), SidebarItem.GROUPS);
    }

    private void onSidebarItemSelected(SidebarItem item) {
        JPanel screen = switch (item) {
            case GROUPS -> new ZoneStageView(tournament);
            /*
            case KNOCKOUT -> new KnockoutStagePanel();
            case TEAMS -> new TeamsListView();
            case REFEREES -> new RefereesListView();
            case PLAYERS -> new PlayersListView();
            case RANKINGS -> new RankingsView();
            case CREDENTIALS -> new CredentialsView();
            case ADMIN -> new AdminView();
             */
            case KNOCKOUT -> null;
            case TEAMS -> null;
            case REFEREES -> null;
            case PLAYERS -> null;
            case RANKINGS -> null;
            case CREDENTIALS -> null;
            case ADMIN -> null;
        };
        showScreen(screen, item);
    }

    public void showScreen(JPanel screen, SidebarItem correspondingItem) {
        contentPanel.removeAll();
        contentPanel.add(screen, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        sidebar.setActiveItem(correspondingItem);
    }

    private void onResetTournament() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Are you sure? The current tournament will be lost.",
                "Reset tournament",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            // TODO acá disparás la lógica real de reset + volver a la pantalla de bienvenida
        }
    }
}