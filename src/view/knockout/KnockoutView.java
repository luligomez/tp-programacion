package view.knockout;

import model.match.knockout.KnockoutTie;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class KnockoutView extends JPanel {

    private final JButton btnSimulate;
    private Runnable onSimulateListener;
    private final JLabel lblPhase = new JLabel("Quarter Finals", SwingConstants.CENTER);
    private JPanel centerPanel;

    public KnockoutView() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 5, 30));

        lblPhase.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPhase.setHorizontalAlignment(SwingConstants.LEFT);
        topPanel.add(lblPhase);

        btnSimulate = new JButton("Simulate Next Round ⚽");
        btnSimulate.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSimulate.setBackground(Color.WHITE);
        btnSimulate.setForeground(Color.BLACK);
        btnSimulate.setOpaque(true);
        btnSimulate.setFocusPainted(false);
        btnSimulate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE, 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btnSimulate.addActionListener(e -> {
            if (onSimulateListener != null) onSimulateListener.run();
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(btnSimulate);
        topPanel.add(buttonWrapper);

        topPanel.add(new JLabel()); // columna vacía para equilibrar

        add(topPanel, BorderLayout.NORTH);
    }

    public void setOnSimulateListener(Runnable listener) {
        this.onSimulateListener = listener;
    }

    public void setPhaseTitle(String title) {
        lblPhase.setText(title);
    }

    public void setSimulateButtonText(String text) {
        btnSimulate.setText(text);
    }


    private static final Color BLUE = new Color(35, 95, 190);

    public void showTies(List<KnockoutTie> ties, boolean firstLegPlayed) {
        if (centerPanel != null) remove(centerPanel);

        centerPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        int number = 1;
        for (KnockoutTie tie : ties) {
            centerPanel.add(new TieCardPanel(tie, number++, firstLegPlayed));
        }

        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void disableSimulateButton() {
        btnSimulate.setEnabled(false);
    }

}