package model.person;

import java.time.LocalDate;

public class FieldPlayer extends Player {
    //attributes
    private int tackling;
    private int speed;
    private int skill;
    private int heading;
    private int finishing;
    private int shotPower;
    private int gameVision;
    private int physicalResistance;
    
    //career stats
    private int careerGoals;
    private int careerPenalties;
    private int careerPenaltiesScored;
    private int careerAssists;

    public FieldPlayer(String firstName, String lastName, LocalDate birthDate,
                       String documentType, String documentNumber,
                       Position position, int rating,
                       int matchesPlayed, int expulsions,
                       int tackling, int speed, int skill, int heading,
                       int finishing, int shotPower, int gameVision,
                       int physicalResistance,
                       int careerGoals, int careerPenalties,
                       int careerPenaltiesScored, int careerAssists) {

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
        this.careerGoals = careerGoals;
        this.careerPenalties = careerPenalties;
        this.careerPenaltiesScored = careerPenaltiesScored;
        this.careerAssists = careerAssists;
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

    public int getCareerGoals() {
        return careerGoals;
    }

    public void setCareerGoals(int careerGoals) {
        this.careerGoals = careerGoals;
    }

    public int getCareerPenalties() {
        return careerPenalties;
    }

    public void setCareerPenalties(int careerPenalties) {
        this.careerPenalties = careerPenalties;
    }

    public int getCareerPenaltiesScored() {
        return careerPenaltiesScored;
    }

    public void setCareerPenaltiesScored(int careerPenaltiesScored) {
        this.careerPenaltiesScored = careerPenaltiesScored;
    }

    public int getCareerAssists() {
        return careerAssists;
    }

    public void setCareerAssists(int careerAssists) {
        this.careerAssists = careerAssists;
    }
}