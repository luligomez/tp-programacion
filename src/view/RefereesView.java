package view;

import model.reports.RefereeReportData;
import model.reports.RefereeReportItem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class RefereesView extends JPanel {

    private final JTable table;
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

        table = new JTable(tableModel);
        setupTableStyle();

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

    private void setupTableStyle() {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.setGridColor(new Color(230, 233, 240));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(220, 235, 252));
        table.setSelectionForeground(TEXT_DARK);


        // Estilo de los encabezados
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(PRIMARY_BLUE);
        header.setForeground(Color.BLUE);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        // Renderizador personalizado para centrar datos y resaltar la FILA EXTRA DE PROMEDIO
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                boolean isSummaryRow = (row == table.getRowCount() - 1);

                if (isSummaryRow) {
                    // Estilo diferenciado para la fila extra del promedio
                    c.setBackground(SUMMARY_ROW_BG);
                    c.setFont(new Font("SansSerif", Font.BOLD, 14));
                    c.setForeground(PRIMARY_BLUE);
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    c.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    c.setForeground(TEXT_DARK);
                }

                // Alineación al centro para columnas numéricas y ranking
                if (column == 0 || column == 3 || column == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return c;
            }
        });
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

