package view.zonestage;

import model.Tournament;

import javax.swing.*;
import java.awt.*;

public class ZoneStageView extends JPanel {

    private Runnable onDrawCompletedListener;
    private Runnable onDrawConfirmedListener;
    private Runnable onRedrawListener;
    private StandingsPanel standingsPanel;

    public ZoneStageView() {
        setLayout(new BorderLayout());
    }

    // Setters de listeners para el controlador
    public void setOnDrawCompletedListener(Runnable listener) {
        this.onDrawCompletedListener = listener;
    }

    public void setOnDrawConfirmedListener(Runnable listener) {
        this.onDrawConfirmedListener = listener;
    }

    public void setOnRedrawListener(Runnable listener) {
        this.onRedrawListener = listener;
    }

    // Métodos de renderizado invocados por el controlador
    public void showDrawPotsState(Tournament tournament) {
        removeAll();
        add(new DrawPotsPanel(tournament, () -> {
            if (onDrawCompletedListener != null) onDrawCompletedListener.run();
        }), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showDrawResultState(Tournament tournament) {
        removeAll();
        add(new DrawResultPanel(
                tournament,
                () -> { if (onDrawConfirmedListener != null) onDrawConfirmedListener.run(); },
        () -> { if (onRedrawListener != null) onRedrawListener.run(); }
        ), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showStandingsState(Tournament tournament) {
        removeAll();
        if (standingsPanel == null) {
            standingsPanel = new StandingsPanel(tournament);
        } else { standingsPanel.updateGroups(tournament);
        }
        add(standingsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    public StandingsPanel getStandingsPanel() {
        return standingsPanel;
    }
}
