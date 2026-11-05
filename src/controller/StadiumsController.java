package controller;

import DAO.CityDAO;
import DAO.StadiumDAO;
import model.place.City;
import model.place.Stadium;
import view.citiesAndStadiums.StadiumsView;
import java.util.function.Consumer;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StadiumsController {

    private final StadiumsView view;
    private final StadiumDAO stadiumDAO;
    private final CityDAO cityDAO;
    private final Consumer<List<Stadium>> onStadiumsLoaded;

    public StadiumsController(StadiumDAO stadiumDAO, CityDAO cityDAO, Consumer<List<Stadium>> onStadiumsLoaded) {
        this.view = new StadiumsView();
        this.stadiumDAO = stadiumDAO;
        this.cityDAO = cityDAO;
        this.onStadiumsLoaded = onStadiumsLoaded;

        view.addAddListener(e -> addStadium());
        view.addEditListener(e -> editStadium());
        view.addDeleteListener(e -> deleteStadium());

        loadCities();
        loadStadiums();
    }

    public StadiumsView getView() {
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
                    view.setCities(get());
                } catch (InterruptedException | ExecutionException ex) {
                    view.showMessage("Error loading cities: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public void loadStadiums() {
        new SwingWorker<List<Stadium>, Void>() {
            @Override
            protected List<Stadium> doInBackground() throws SQLException, InterruptedException {
                return stadiumDAO.getAllStadiums();
            }

            @Override
            protected void done() {
                try {
                    List<Stadium> stadiums = get();
                    view.setStadiumsData(stadiums);
                    onStadiumsLoaded.accept(stadiums);
                } catch (InterruptedException | ExecutionException ex) {
                    view.showMessage("Error loading stadiums: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void addStadium() {
        String name = view.getNameText();
        int capacity = parseCapacity();
        City city = view.getSelectedCity();

        if (name.isEmpty() || capacity <= 0 || city == null) {
            view.showMessage("Name, a valid capacity and a city are required.");
            return;
        }

        try {
            stadiumDAO.insertStadium(name, capacity, city.getId());
            view.clearFields();
            loadStadiums();
        } catch (SQLException ex) {
            view.showMessage("Error adding stadium: " + ex.getMessage());
        }
    }

    private void editStadium() {
        long id = view.getSelectedStadiumId();
        if (id < 0) {
            view.showMessage("Select a stadium first.");
            return;
        }

        String name = view.getNameText();
        int capacity = parseCapacity();
        City city = view.getSelectedCity();

        if (name.isEmpty() || capacity <= 0 || city == null) {
            view.showMessage("Name, a valid capacity and a city are required.");
            return;
        }

        try {
            stadiumDAO.updateStadium(id, name, capacity, city.getId());
            view.clearFields();
            loadStadiums();
        } catch (SQLException ex) {
            view.showMessage("Error updating stadium: " + ex.getMessage());
        }
    }

    private void deleteStadium() {
        long id = view.getSelectedStadiumId();
        if (id < 0) {
            view.showMessage("Select a stadium first.");
            return;
        }

        try {
            stadiumDAO.deleteStadium(id);
            view.clearFields();
            loadStadiums();
        } catch (SQLException ex) {
            view.showMessage("Error deleting stadium: " + ex.getMessage());
        }
    }

    private int parseCapacity() {
        try {
            return Integer.parseInt(view.getCapacityText());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}