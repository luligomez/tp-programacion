package view.mainwindow;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {
    public TopBar(Runnable onResetClicked) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 50));
        setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("  International Clubs Cup");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.WEST);

        JButton settingsButton = new JButton("⚙ Settings");
        settingsButton.addActionListener(e -> showSettingsMenu(settingsButton, onResetClicked));

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.add(settingsButton);
        add(rightPanel, BorderLayout.EAST);
    }

    private void showSettingsMenu(Component anchor, Runnable onResetClicked) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem resetItem = new JMenuItem("Reset Tournament");
        resetItem.addActionListener(e -> onResetClicked.run());
        menu.add(resetItem);
        menu.show(anchor, 0, anchor.getHeight());
    }
}