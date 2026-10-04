package model.reports;

import java.util.List;

public class TeamReportData {
    private final List<TeamReportItem> items;

    public TeamReportData(List<TeamReportItem> items) {
        this.items = items;
    }

    public List<TeamReportItem> getItems() {
        return items;
    }
}
