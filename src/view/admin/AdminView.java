package view.admin;

import view.theme.Theme;
import javax.swing.*;
import java.awt.*;

public class AdminView extends JPanel {

    public AdminView(Component citiesView, Component stadiumsView, Runnable onStadiumsTabSelected) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cities", citiesView);
        tabs.addTab("Stadiums", stadiumsView);

        styleTabs(tabs);

        // Evento al cambiar de pestaña
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1 && onStadiumsTabSelected != null) {
                onStadiumsTabSelected.run();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    private void styleTabs(JTabbedPane tabs) {
        tabs.setFont(Theme.BIG_BOLD16);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            JLabel label = new JLabel(tabs.getTitleAt(i));
            label.setFont(Theme.BIG_BOLD16);
            label.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
            tabs.setTabComponentAt(i, label);
        }
    }
}