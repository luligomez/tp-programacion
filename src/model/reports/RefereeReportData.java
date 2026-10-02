package model.reports;

import java.util.List;

public class RefereeReportData{
    private List<RefereeReportItem> items;
    private double averageYears;

    public RefereeReportData(List<RefereeReportItem> items, double averageYears) {
        this.items = items;
        this.averageYears = averageYears;
    }

    public List<RefereeReportItem> getItems() {
        return items;
    }

    public double getAverageYears() {
        return averageYears;
    }

    public void setItems(List<RefereeReportItem> items) {
        this.items = items;
    }

    public void setAverageYears(double averageYears) {
        this.averageYears = averageYears;
    }
}
