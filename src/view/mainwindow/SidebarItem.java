package view.mainwindow;

public enum SidebarItem {
    GROUPS("Groups"),
    KNOCKOUT("Knockout Stage"),
    TEAMS("Teams"),
    PLAYERS("Players"),
    REFEREES("Referees"),
    RANKINGS("Rankings"),
    CREDENTIALS("Credentials"),
    ADMIN("Cities & Stadiums");

    private final String label;

    SidebarItem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
