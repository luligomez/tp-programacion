package view.components;

import view.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class StyledTable extends JTable {

    public StyledTable(TableModel model) {
        super(model);
        setupStandardStyle();
    }

    private void setupStandardStyle() {
        setFont(Theme.BODY);
        setRowHeight(32);
        setGridColor(Theme.BACKGROUND);
        setShowVerticalLines(false);
        setSelectionBackground(Theme.SELECTION);
        setSelectionForeground(Theme.TEXT);

        // 🔒 Bloquear reordenamiento de columnas globalmente
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setPreferredSize(new Dimension(0, 38));
        getTableHeader().setResizingAllowed(false);

        // 🎨 Aplicar el header azul estilizado
        applyHeaderStyle();
    }

    public void applyHeaderStyle() {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(Theme.PRIMARY);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(Theme.BODY_BOLD);
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

    public void setColumnSuffix(int columnIndex, String suffix) {
        getColumnModel().getColumn(columnIndex).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText(value + suffix);
                } else {
                    super.setValue(value);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        });
    }

    public void setNumericFormatWithFallback(int columnIndex, String formatPattern) {
        setNumericFormatWithFallback(columnIndex, formatPattern, null);
    }

    public void setNumericFormatWithFallback(int columnIndex, String formatPattern, String suffix) {
        getColumnModel().getColumn(columnIndex).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number num) {
                    if (num.doubleValue() < 0) {
                        setText("-");                       // se mantiene tu fallback
                    } else {
                        String text = (formatPattern != null)
                                ? String.format(formatPattern, num.doubleValue())
                                : String.valueOf(num.intValue());
                        setText(suffix != null ? text + suffix : text);
                    }
                } else {
                    super.setValue(value);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        });
    }

}
