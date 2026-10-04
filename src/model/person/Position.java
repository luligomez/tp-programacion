package model.person;

public enum Position {

    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD;

    @Override public String toString() { 
        return switch (this) { 
            case GOALKEEPER -> "Goalkeeper"; 
            case DEFENDER -> "Defender"; 
            case MIDFIELDER -> "Midfielder"; 
            case FORWARD -> "Forward"; }; }

}