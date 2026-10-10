package view.zonestage;

import model.Team;
import model.match.Match;
import model.zone.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ZoneStageView extends JPanel {

    private Runnable onDrawRequestedListener;
    private Runnable onDrawConfirmedListener;
    private Runnable onRedrawListener;
    private Runnable onSimulateMatchdayListener;
    private StandingsPanel standingsPanel;
    private Consumer<Match> matchSelectedListener;

    public ZoneStageView() {
        setLayout(new BorderLayout());
    }

    public void setOnDrawRequestedListener(Runnable listener) {
        this.onDrawRequestedListener = listener;
    }

    public void setOnDrawConfirmedListener(Runnable listener) {
        this.onDrawConfirmedListener = listener;
    }

    public void setOnRedrawListener(Runnable listener) {
        this.onRedrawListener = listener;
    }

    public void setOnSimulateMatchdayListener(Runnable listener) {
        this.onSimulateMatchdayListener = listener;
    }

    public void showDrawPotsState(List<List<Team>> pots) {
        removeAll();
        add(new DrawPotsPanel(pots, () -> {
            if (onDrawRequestedListener != null) onDrawRequestedListener.run();
        }), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showDrawResultState(List<Zone> zones) {
        removeAll();
        add(new DrawResultPanel(
                zones,
                () -> { if (onDrawConfirmedListener != null) onDrawConfirmedListener.run(); },
                () -> { if (onRedrawListener != null) onRedrawListener.run(); }
        ), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showStandingsState(List<Zone> zones) {
        removeAll();
        if (standingsPanel == null) {
            standingsPanel = new StandingsPanel(zones);
            if (onSimulateMatchdayListener != null) {
                standingsPanel.setOnSimulateMatchdayListener(onSimulateMatchdayListener);
            }
            // la lambda lee el campo al momento del click, no al construir
            standingsPanel.setOnMatchSelectedListener(match -> {
                if (matchSelectedListener != null) matchSelectedListener.accept(match);
            });
        } else {
            standingsPanel.updateGroups(zones);
        }
        add(standingsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void updateStandings(List<Zone> zones) {
        if (standingsPanel != null) {
            standingsPanel.updateGroups(zones);
        }
    }

    public void setMatchdayLabel(int matchday) {
        if (standingsPanel != null) {
            standingsPanel.setMatchdayLabel(matchday);
        }
    }

    public void disableSimulateButton() {
        if (standingsPanel != null) {
            standingsPanel.disableSimulateButton();
        }
    }

    public StandingsPanel getStandingsPanel() {
        return standingsPanel;
    }

    public void setOnMatchSelectedListener(Consumer<Match> listener) {
        this.matchSelectedListener = listener;
    }
}