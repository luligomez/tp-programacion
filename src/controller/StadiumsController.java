package controller;

import DAO.CityDAO;
import DAO.StadiumDAO;
import model.place.City;
import view.citiesAndStadiums.StadiumsView;

import java.sql.SQLException;

public class StadiumsController {

    private final StadiumsView view;
    private final StadiumDAO stadiumDAO;
    private final CityDAO cityDAO;

    public StadiumsController(StadiumDAO stadiumDAO, CityDAO cityDAO) {
        this.view = new StadiumsView();
        this.stadiumDAO = stadiumDAO;
        this.cityDAO = cityDAO;

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
        try {
            view.setCities(cityDAO.getAllCities());
        } catch (SQLException ex) {
            view.showMessage("Error loading cities: " + ex.getMessage());
        }
    }

    public void loadStadiums() {
        try {
            view.setStadiumsData(stadiumDAO.getAllStadiums());
        } catch (SQLException ex) {
            view.showMessage("Error loading stadiums: " + ex.getMessage());
        }
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