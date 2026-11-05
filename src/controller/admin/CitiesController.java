package controller.admin;

import DAO.CityDAO;
import model.place.City;
import view.admin.CitiesView;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class CitiesController {

    private final CitiesView view;
    private final CityDAO cityDAO;

    public CitiesController(CityDAO cityDAO) {
        this.view = new CitiesView();
        this.cityDAO = cityDAO;

        view.addAddListener(e -> addCity());
        view.addEditListener(e -> editCity());
        view.addDeleteListener(e -> deleteCity());

        loadCities();
    }

    public CitiesView getView() {
        return view;
    }

    public void loadCities() {
        new SwingWorker<List<City>, Void>() {
            @Override
            protected List<City> doInBackground() throws SQLException {
                return cityDAO.getAllCities();
            }

            @Override
            protected void done() {
                try {
                    view.setCitiesData(get());
                } catch (InterruptedException | ExecutionException ex) {
                    view.showMessage("Error loading cities: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void addCity() {
        String name = view.getNameText();
        String country = view.getCountryText();

        if (name.isEmpty() || country.isEmpty()) {
            view.showMessage("Name and country are required.");
            return;
        }

        try {
            cityDAO.insertCity(name, country);
            view.clearFields();
            loadCities();
        } catch (SQLException ex) {
            view.showMessage("Error adding city: " + ex.getMessage());
        }
    }

    private void editCity() {
        long id = view.getSelectedCityId();
        if (id < 0) {
            view.showMessage("Select a city first.");
            return;
        }

        String name = view.getNameText();
        String country = view.getCountryText();

        if (name.isEmpty() || country.isEmpty()) {
            view.showMessage("Name and country are required.");
            return;
        }

        try {
            cityDAO.updateCity(id, name, country);
            view.clearFields();
            loadCities();
        } catch (SQLException ex) {
            view.showMessage("Error updating city: " + ex.getMessage());
        }
    }

    private void deleteCity() {
        long id = view.getSelectedCityId();
        if (id < 0) {
            view.showMessage("Select a city first.");
            return;
        }

        try {
            if (cityDAO.deleteCity(id)) {
                view.clearFields();
                loadCities();
            } else {
                view.showMessage("This city has stadiums, so it cannot be deleted.");
            }
        } catch (SQLException ex) {
            view.showMessage("Error deleting city: " + ex.getMessage());
        }
    }
}