package model.person;

import java.time.LocalDate;

public class FieldPlayer extends Player {

    private int tackling;
    private int speed;
    private int skill;
    private int heading;
    private int finishing;
    private int shotPower;
    private int gameVision;
    private int physicalResistance;

    private int goals;
    private int penalties;
    private int penaltiesScored;
    private int assists;

    public FieldPlayer(String firstName, String lastName, LocalDate birthDate,
                       String documentType, String documentNumber,
                       Position position, int rating,
                       int matchesPlayed, int expulsions,
                       int tackling, int speed, int skill, int heading,
                       int finishing, int shotPower, int gameVision,
                       int physicalResistance,
                       int goals, int penalties,
                       int penaltiesScored, int assists) {

        super(firstName, lastName, birthDate, documentType, documentNumber,
                position, rating, matchesPlayed, expulsions);

        this.tackling = tackling;
        this.speed = speed;
        this.skill = skill;
        this.heading = heading;
        this.finishing = finishing;
        this.shotPower = shotPower;
        this.gameVision = gameVision;
        this.physicalResistance = physicalResistance;
        this.goals = goals;
        this.penalties = penalties;
        this.penaltiesScored = penaltiesScored;
        this.assists = assists;
    }

    public int getTackling() {
        return tackling;
    }

    public void setTackling(int tackling) {
        this.tackling = tackling;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getFinishing() {
        return finishing;
    }

    public void setFinishing(int finishing) {
        this.finishing = finishing;
    }

    public int getShotPower() {
        return shotPower;
    }

    public void setShotPower(int shotPower) {
        this.shotPower = shotPower;
    }

    public int getGameVision() {
        return gameVision;
    }

    public void setGameVision(int gameVision) {
        this.gameVision = gameVision;
    }

    public int getPhysicalResistance() {
        return physicalResistance;
    }

    public void setPhysicalResistance(int physicalResistance) {
        this.physicalResistance = physicalResistance;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getPenalties() {
        return penalties;
    }

    public void setPenalties(int penalties) {
        this.penalties = penalties;
    }

    public int getPenaltiesScored() {
        return penaltiesScored;
    }

    public void setPenaltiesScored(int penaltiesScored) {
        this.penaltiesScored = penaltiesScored;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }
}