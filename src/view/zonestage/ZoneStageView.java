package view.zonestage;

import model.Tournament;

import javax.swing.*;
import java.awt.*;

public class ZoneStageView extends JPanel {
    private Tournament tournament;

    public ZoneStageView(Tournament tournament) {
        this.tournament = tournament;
        setLayout(new BorderLayout());
        showCurrentState();
    }

    private void showCurrentState() {
        removeAll();
        if (!tournament.hasZonesDrawn()) {
            add(new DrawPotsPanel(tournament, this::onDrawCompleted), BorderLayout.CENTER);
        } else if (!tournament.isDrawConfirmed()) {
            add(new DrawResultPanel(tournament, this::onDrawConfirmed, this::onRedraw), BorderLayout.CENTER);
        } else {
            add(new JLabel("TODO: Standings View", SwingConstants.CENTER), BorderLayout.CENTER); // placeholder por ahora
        }
        revalidate();
        repaint();
    }

    private void onDrawCompleted() {
        showCurrentState();
    }

    private void onDrawConfirmed() {
        tournament.confirmDraw();
        showCurrentState();
    }

    private void onRedraw() {
        tournament.resetDraw();
        showCurrentState();
    }
}
