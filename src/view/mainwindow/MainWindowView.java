package view.mainwindow;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainWindowView extends JFrame {
    private TopBar topBar;
    private Sidebar sidebar;
    private JPanel contentPanel;

    private Consumer<SidebarItem> sidebarListener;
    private Runnable resetListener;

    public MainWindowView() {
        super("International Clubs Cup");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        topBar = new TopBar(() -> {
            if (resetListener != null) resetListener.run();
        });
        add(topBar, BorderLayout.NORTH);

        sidebar = new Sidebar(item -> {
            if (sidebarListener != null) sidebarListener.accept(item);
        });
        add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setOnSidebarSelectListener(Consumer<SidebarItem> listener) {
        this.sidebarListener = listener;
    }

    public void setOnResetListener(Runnable listener) {
        this.resetListener = listener;
    }

    public void showScreen(JPanel screen, SidebarItem correspondingItem) {
        contentPanel.removeAll();
        contentPanel.add(screen, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        sidebar.setActiveItem(correspondingItem);
    }
}
