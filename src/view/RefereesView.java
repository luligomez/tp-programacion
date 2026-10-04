package view;

import model.reports.RefereeReportData;
import model.reports.RefereeReportItem;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class RefereesView extends JPanel {

    private final StyledTable table;
    private final DefaultTableModel tableModel;
    private final JLabel lblAverageYears;
    private final JLabel lblTotalReferees;

    // Paleta de colores consistente con la interfaz
    private static final Color PRIMARY_BLUE = new Color(35, 95, 190);
    private static final Color BACKGROUND_GRAY = new Color(245, 247, 250);
    private static final Color SUMMARY_ROW_BG = new Color(230, 240, 255);
    private static final Color TEXT_DARK = new Color(30, 40, 50);

    public RefereesView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // --- 1. ENCABEZADO DE LA PANTALLA ---
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. MODELO Y TABLA ---
        String[] columns = {"Rank", "Referee Name", "Nationality", "Matches Officiated", "Years as Referee"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }

        };

        table = new StyledTable(tableModel);
        table.centerAllRows();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220), 1));
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
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Ranked by total matches officiated in the tournament");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
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

        // Card con el total y promedio resaltado
        JPanel summaryCard = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryCard.setBackground(Color.WHITE);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        JLabel lblText = new JLabel("Global Average Experience:");
        lblText.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblText.setForeground(TEXT_DARK);

        summaryCard.add(lblText);
        summaryCard.add(lblAverageYears);

        footer.add(summaryCard);
        return footer;
    }

    // --- MÉTODOS PARA POBLAR LA TABLA DESDE EL CONTROLADOR ---

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

        // 👈 AQUÍ SE AGREGA LA FILA EXTRA EXIGIDA POR LA CÁTEDRA
        tableModel.addRow(new Object[]{
                "---",
                "AVERAGE (All Referees)",
                "---",
                "---",
                String.format("%.2f yrs", averageYears)
        });

        // Actualizar etiqueta del pie de página
        lblAverageYears.setText(String.format("%.2f yrs", averageYears));
        lblAverageYears.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblAverageYears.setForeground(PRIMARY_BLUE);
    }

}

