package view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class StyledTable extends JTable {

    private static final Color PRIMARY_BLUE = new Color(35, 95, 190);
    private static final Color TEXT_DARK = new Color(30, 40, 50);

    public StyledTable(TableModel model) {
        super(model);
        setupStandardStyle();
    }

    private void setupStandardStyle() {
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setRowHeight(32);
        setGridColor(new Color(230, 233, 240));
        setShowVerticalLines(false);
        setSelectionBackground(new Color(220, 235, 252));
        setSelectionForeground(TEXT_DARK);

        // 🔒 Bloquear reordenamiento de columnas globalmente
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setPreferredSize(new Dimension(0, 38));
        getTableHeader().setResizingAllowed(false);

        // 🎨 Aplicar el header azul estilizado
        applyHeaderStyle();
    }

    public void applyHeaderStyle() {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(PRIMARY_BLUE);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    public void centerAllRows(){
        for (int i = 0; i < getColumnCount(); i++) {
            setColumnAlignment(i,SwingConstants.CENTER);
        }
    }

    // Funcion de conveniencia para alinear columnas específicas (Centro, Izquierda, Derecha)
    public void setColumnAlignment(int columnIndex, int alignment) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }
    
    public void setColumnWidths(int[] widths) {
        for (int i = 0; i < widths.length && i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

}
