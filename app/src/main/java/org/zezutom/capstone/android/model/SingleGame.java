package org.zezutom.capstone.android.model;

public class SingleGame {

    private int round;

    private int score;

    private int powerUps;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(int powerUps) {
        this.powerUps = powerUps;
    }

    public int nextRound() {
        return ++round;
    }
}
