package view.rankings;

import view.components.StyledTable;
import view.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Common structure of every ranking screen: title, table and a message when there is no data.
// Each ranking only defines its title, its columns and how an item becomes a table row.
public abstract class RankingView<T> extends JPanel {
    private final DefaultTableModel tableModel;
    private final JLabel emptyMessage;

    protected RankingView(String title, String emptyText) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.TITLE);
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(columns(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        StyledTable table = new StyledTable(tableModel);
        table.centerAllRows();
        configureTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        emptyMessage = new JLabel(emptyText, SwingConstants.CENTER);
        emptyMessage.setFont(Theme.BIG_BOLD16);
    }

    // Column names of the table (the first one is usually "Position")
    protected abstract String[] columns();

    // Values of one table row, without the position
    protected abstract Object[] rowOf(T item);

    // Optional table adjustments (column widths, suffixes...)
    protected void configureTable(StyledTable table) {
    }

    public void showRanking(List<T> ranking) {
        tableModel.setRowCount(0);

        int position = 1;
        for (T item : ranking) {
            Object[] values = rowOf(item);
            Object[] row = new Object[values.length + 1];
            row[0] = position++;
            System.arraycopy(values, 0, row, 1, values.length);
            tableModel.addRow(row);
        }

        if (ranking.isEmpty()) {
            add(emptyMessage, BorderLayout.SOUTH);
        } else {
            remove(emptyMessage);
        }
        revalidate();
        repaint();
    }
}