package view.knockout;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public class PhaseStepBar extends JPanel {

    private static final Color BLUE = new Color(35, 95, 190);
    private static final Color BAR_BG = new Color(225, 225, 225);
    private static final Color LOCKED_FG = new Color(160, 160, 160);

    private final JButton[] steps;
    private IntConsumer onStepSelected;

    public PhaseStepBar(String... labels) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel bar = new JPanel(new GridLayout(1, labels.length, 0, 0));
        bar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        steps = new JButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            JButton step = new JButton(labels[i]);
            step.setFont(new Font("SansSerif", Font.BOLD, 14));
            step.setFocusPainted(false);
            step.setBorderPainted(false);
            step.setOpaque(true);
            step.setPreferredSize(new Dimension(170, 40));
            step.addActionListener(e -> {
                if (onStepSelected != null) onStepSelected.accept(index);
            });
            steps[i] = step;
            bar.add(step);
        }
        add(bar);
    }

    public void setOnStepSelectedListener(IntConsumer listener) {
        this.onStepSelected = listener;
    }

    // activeIndex: la fase que se está viendo
    // lastUnlockedIndex: hasta qué fase se puede entrar
    public void update(int activeIndex, int lastUnlockedIndex) {
        for (int i = 0; i < steps.length; i++) {
            JButton step = steps[i];
            boolean unlocked = i <= lastUnlockedIndex;
            step.setEnabled(unlocked);

            if (i == activeIndex) {
                step.setBackground(BLUE);
                step.setForeground(Color.WHITE);
            } else {
                step.setBackground(BAR_BG);
                step.setForeground(unlocked ? Color.BLACK : LOCKED_FG);
            }
        }
    }
}