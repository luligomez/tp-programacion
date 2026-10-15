package model.person;

public enum Position {

    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD;

    @Override
    public String toString() {
        return switch (this) { 
            case GOALKEEPER -> "Goalkeeper"; 
            case DEFENDER -> "Defender"; 
            case MIDFIELDER -> "Midfielder"; 
            case FORWARD -> "Forward"; };
    }

    public String getShortName() {
        return switch (this) {
            case GOALKEEPER -> "GK";
            case DEFENDER -> "DF";
            case MIDFIELDER -> "MF";
            case FORWARD -> "FW";
        };
    }

}