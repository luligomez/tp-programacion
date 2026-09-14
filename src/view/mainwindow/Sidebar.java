package view.mainwindow;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Sidebar extends JPanel {
    private Consumer<SidebarItem> onItemSelected;
    private Map<SidebarItem, JButton> buttons = new HashMap<>();
    private SidebarItem activeItem;

    public Sidebar(Consumer<SidebarItem> onItemSelected) {
        this.onItemSelected = onItemSelected;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(45, 45, 45));

        for (SidebarItem item : SidebarItem.values()) {
            JButton button = createSidebarButton(item);
            buttons.put(item, button);
            add(button);
        }
    }

    private JButton createSidebarButton(SidebarItem item) {
        JButton button = new JButton(item.getLabel());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBackground(new Color(45, 45, 45));
        button.addActionListener(e -> onItemSelected.accept(item));
        return button;
    }

    public void setActiveItem(SidebarItem item) {
        if (activeItem != null) {
            buttons.get(activeItem).setBackground(new Color(45, 45, 45));
        }
        activeItem = item;
        buttons.get(item).setBackground(new Color(70, 70, 70));
    }
}
