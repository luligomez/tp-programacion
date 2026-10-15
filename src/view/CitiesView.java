package view;

import model.place.City; // adjust the import if City is in another package
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CitiesView extends JPanel {

    private final StyledTable table;
    private final DefaultTableModel tableModel;

    private final JTextField txtName = new JTextField(15);
    private final JTextField txtCountry = new JTextField(15);
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");

    private static final Color BACKGROUND_GRAY = new Color(245, 247, 250);
    private static final Color TEXT_DARK = new Color(30, 40, 50);

    public CitiesView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        add(createHeaderPanel(), BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Country"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new StyledTable(tableModel);
        table.centerAllRows();
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When a row is selected, copy its values into the text fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int row = table.convertRowIndexToModel(viewRow);
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtCountry.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        add(createFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Cities");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Add, edit or delete the cities that host stadiums");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Country:"));
        panel.add(txtCountry);
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        return panel;
    }

    public void setCitiesData(List<City> cities) {
        tableModel.setRowCount(0);
        for (City city : cities) {
            tableModel.addRow(new Object[]{
                    city.getId(),
                    city.getName(),
                    city.getCountry()
            });
        }
    }

    public String getNameText() {
        return txtName.getText().trim();
    }

    public String getCountryText() {
        return txtCountry.getText().trim();
    }

    public long getSelectedCityId() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return -1;
        }
        int row = table.convertRowIndexToModel(viewRow);
        return Long.parseLong(tableModel.getValueAt(row, 0).toString());
    }

    public void clearFields() {
        txtName.setText("");
        txtCountry.setText("");
        table.clearSelection();
    }

    public void addAddListener(ActionListener l) {
        btnAdd.addActionListener(l);
    }

    public void addEditListener(ActionListener l) {
        btnEdit.addActionListener(l);
    }

    public void addDeleteListener(ActionListener l) {
        btnDelete.addActionListener(l);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}