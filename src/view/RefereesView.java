package view;

import model.reports.referee.RefereeReportData;
import model.reports.referee.RefereeReportItem;
import view.components.StyledTable;
import view.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RefereesView extends JPanel {

    private final StyledTable table;
    private final DefaultTableModel tableModel;
    private final JLabel lblAverageYears;
    private final JLabel lblTotalReferees;

    public RefereesView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // --- 1. SCREEN HEADER ---
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. MODELO Y TABLA ---
        String[] columns = {"Rank", "Referee Name", "Nationality", "Matches Officiated", "Years as Referee"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }

        };

        table = new StyledTable(tableModel);
        table.centerAllRows();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // --- 3. PANEL INFERIOR (TARJETAS DE RESUMEN) ---
        lblAverageYears = new JLabel("0.0 yrs", SwingConstants.CENTER);
        lblTotalReferees = new JLabel("0", SwingConstants.CENTER);
        JPanel footerPanel = createFooterPanel();

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Report VII: Referee Ranking & Experience");
        lblTitle.setFont(Theme.TITLE);
        lblTitle.setForeground(Theme.TEXT);

        JLabel lblSubtitle = new JLabel("Ranked by total matches officiated in the tournament");
        lblSubtitle.setFont(Theme.SUBTITLE);
        lblSubtitle.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setOpaque(false);

        // Card with the highlighted total and average
        JPanel summaryCard = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryCard.setBackground(Theme.PRIMARY_LIGHT);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        JLabel lblText = new JLabel("Global Average Experience:");
        lblText.setFont(Theme.SMALL_BOLD13);
        lblText.setForeground(Theme.TEXT);

        summaryCard.add(lblText);
        summaryCard.add(lblAverageYears);

        footer.add(summaryCard);
        return footer;
    }

    // --- METHODS TO POPULATE THE TABLE FROM THE CONTROLLER ---

    public void setRefereesData(RefereeReportData data) {
        List<RefereeReportItem> referees = data.getItems();
        double averageYears= data.getAverageYears();
        tableModel.setRowCount(0); // Limpiar tabla

        int rank = 1;
        for (RefereeReportItem ref : referees) {
            tableModel.addRow(new Object[]{
                    "#" + rank++,
                    ref.getName(),
                    ref.getCountry(),
                    ref.getMatchesOfficiated(),
                    ref.getYearsAsReferee() + " years"
            });
        }

        // Extra row required by the course
        tableModel.addRow(new Object[]{
                "---",
                "AVERAGE (All Referees)",
                "---",
                "---",
                String.format("%.2f yrs", averageYears)
        });

        // Update the footer label
        lblAverageYears.setText(String.format("%.2f yrs", averageYears));
        lblAverageYears.setFont(Theme.BODY_BOLD);
        lblAverageYears.setForeground(Theme.PRIMARY);
    }

}

