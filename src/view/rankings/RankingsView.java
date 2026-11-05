package view.rankings;

import view.theme.Theme;
import javax.swing.*;
import java.awt.*;

public class RankingsView extends JPanel {

    public RankingsView(Component scorerRankingView, Component fairPlayRankingView, Component mostMinutesRankingView) {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Top Scorers", scorerRankingView);
        tabs.addTab("Fair Play", fairPlayRankingView);
        tabs.addTab("Most Minutes", mostMinutesRankingView);

        styleTabs(tabs);
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
