package model.reports;

import java.util.List;

public class PlayerReportData {
    private final List<PlayerReportItem> items;

    public PlayerReportData(List<PlayerReportItem> items) {
        this.items = items;
    }

    public List<PlayerReportItem> getItems() {
        return items;
    }
}
