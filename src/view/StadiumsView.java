package view;

import model.place.City;
import model.place.Stadium;
import view.components.StyledTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class StadiumsView extends JPanel {

    private final StyledTable table;
    private final DefaultTableModel tableModel;

    private final JTextField txtName = new JTextField(15);
    private final JTextField txtCapacity = new JTextField(8);
    private final JComboBox<City> cmbCity = new JComboBox<>();
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");

    private static final Color BACKGROUND_GRAY = new Color(245, 247, 250);
    private static final Color TEXT_DARK = new Color(30, 40, 50);

    public StadiumsView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        add(createHeaderPanel(), BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Capacity", "City"};
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

        // The combo shows "Name (Country)" instead of City's default toString
        cmbCity.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof City) {
                    setText(cityLabel((City) value));
                }
                return this;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    int row = table.convertRowIndexToModel(viewRow);
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtCapacity.setText(tableModel.getValueAt(row, 2).toString());
                    selectCityByLabel(tableModel.getValueAt(row, 3).toString());
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

        JLabel lblTitle = new JLabel("Stadiums");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Add, edit or delete stadiums and assign them to a city");
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
        panel.add(new JLabel("Capacity:"));
        panel.add(txtCapacity);
        panel.add(new JLabel("City:"));
        panel.add(cmbCity);
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        return panel;
    }

    private String cityLabel(City city) {
        return city == null ? "" : city.getName() + " (" + city.getCountry() + ")";
    }

    private void selectCityByLabel(String label) {
        for (int i = 0; i < cmbCity.getItemCount(); i++) {
            if (cityLabel(cmbCity.getItemAt(i)).equals(label)) {
                cmbCity.setSelectedIndex(i);
                return;
            }
        }
    }

    public void setStadiumsData(List<Stadium> stadiums) {
        tableModel.setRowCount(0);
        for (Stadium stadium : stadiums) {
            tableModel.addRow(new Object[]{
                    stadium.getId(),
                    stadium.getName(),
                    stadium.getCapacity(),
                    cityLabel(stadium.getCity())
            });
        }
    }

    public void setCities(List<City> cities) {
        cmbCity.removeAllItems();
        for (City city : cities) {
            cmbCity.addItem(city);
        }
    }

    public String getNameText() {
        return txtName.getText().trim();
    }

    public String getCapacityText() {
        return txtCapacity.getText().trim();
    }

    public City getSelectedCity() {
        return (City) cmbCity.getSelectedItem();
    }

    public long getSelectedStadiumId() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return -1;
        }
        int row = table.convertRowIndexToModel(viewRow);
        return Long.parseLong(tableModel.getValueAt(row, 0).toString());
    }

    public void clearFields() {
        txtName.setText("");
        txtCapacity.setText("");
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