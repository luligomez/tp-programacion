package model.person.player;

import model.person.Position;

public class RatingCalculator {

    private static final double STATS_WEIGHT = 0.3;
    private static final double ATTRIBUTES_WEIGHT = 0.7;

    public static int calculateRating(Attributes attributes, CareerStats careerStats, Position position) {
        return (int) Math.round(attributes.calculateScore(position) * ATTRIBUTES_WEIGHT + careerStats.calculateScore(position) * STATS_WEIGHT);
    }
}
