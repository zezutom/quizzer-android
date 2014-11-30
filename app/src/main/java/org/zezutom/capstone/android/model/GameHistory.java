package org.zezutom.capstone.android.model;

public class GameHistory {

    private int powerUps;

    private int oneTimeAttempts;

    private int twoTimeAttempts;

    protected void incrementPowerUps() {
        powerUps++;
    }

    protected void incrementOneTimeAttempts() {
        oneTimeAttempts++;
    }

    protected void incrementTwoTimeAttempts() {
        twoTimeAttempts++;
    }

    public int getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(int powerUps) {
        this.powerUps = powerUps;
    }

    public int getOneTimeAttempts() {
        return oneTimeAttempts;
    }

    public void setOneTimeAttempts(int oneTimeAttempts) {
        this.oneTimeAttempts = oneTimeAttempts;
    }

    public int getTwoTimeAttempts() {
        return twoTimeAttempts;
    }

    public void setTwoTimeAttempts(int twoTimeAttempts) {
        this.twoTimeAttempts = twoTimeAttempts;
    }
}
