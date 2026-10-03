package view.knockout;

import model.match.knockout.FinalMatch;
import model.match.knockout.KnockoutStep;
import model.match.knockout.KnockoutTie;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static model.match.knockout.KnockoutStep.SEMIS_SECOND_LEG;

public class KnockoutView extends JPanel {

    private static final Color BLUE = new Color(35, 95, 190);

    private final JButton btnSimulate;
    private final BracketPanel bracketPanel = new BracketPanel();
    private Runnable onSimulateListener;

    public KnockoutView() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 5, 30));

        JLabel lblTitle = new JLabel("Knockout Stage");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        topPanel.add(lblTitle);

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

        JScrollPane scroll = new JScrollPane(bracketPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void setOnSimulateListener(Runnable listener) {
        this.onSimulateListener = listener;
    }

    public void setStep(KnockoutStep step) {
        btnSimulate.setText(switch (step) {
            case QUARTERS_FIRST_LEG -> "Simulate Quarter Finals: First Leg ⚽";
            case QUARTERS_SECOND_LEG -> "Simulate Quarter Finals: Second Leg ⚽";
            case QUARTERS_PENALTIES -> "Simulate Quarter Finals: Penalties ⚽";
            case SEMIS_FIRST_LEG -> "Simulate Semi Finals: First Leg ⚽";
            case SEMIS_SECOND_LEG -> "Simulate Semi Finals: Second Leg ⚽";
            case SEMIS_PENALTIES -> "Simulate Semi Finals: Penalties ⚽";
            case FINAL -> "Simulate Final ⚽";
            case FINAL_PENALTIES -> "Simulate Final: Penalties ⚽";
            case DONE -> "Tournament completed 🏁";
        });
        btnSimulate.setEnabled(step != KnockoutStep.DONE);
    }

    public void enableSimulateButton() {
        btnSimulate.setEnabled(true);
    }

    public void disableSimulateButton() {
        btnSimulate.setEnabled(false);
    }

    public void showBracket(List<KnockoutTie> quarters, List<KnockoutTie> semis, FinalMatch finalMatch) {
        bracketPanel.update(quarters, semis, finalMatch);
    }
}