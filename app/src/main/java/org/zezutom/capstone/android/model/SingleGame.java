package org.zezutom.capstone.android.model;

public class SingleGame {

    private int round = 1;

    private int score;

    private int powerUps;

    private int firstTimeCorrectAttempts;

    private int remainingAttempts = 3;

    private boolean gameOver;

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

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    public int getFirstTimeCorrectAttempts() {
        return firstTimeCorrectAttempts;
    }

    public void setFirstTimeCorrectAttempts(int firstTimeCorrectAttempts) {
        this.firstTimeCorrectAttempts = firstTimeCorrectAttempts;
    }

    public void subtractAttempt() {
        remainingAttempts--;
        if (remainingAttempts <= 0) {
            gameOver = true;
        }
    }

    public void subtractPowerUps() {
        powerUps--;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int nextRound() {
        return ++round;
    }

    public void score() {
        switch (remainingAttempts) {
            case 3:
                score += 5;
                firstTimeCorrectAttempts++;
                break;
            case 2:
                score += 3;
                break;
            case 1:
                score++;
                break;
            default:
                gameOver = true;
        }

        // Increase power ups if there have been at least two consecutive correct attempts
        if (firstTimeCorrectAttempts > 1) {
            powerUps++;
            firstTimeCorrectAttempts = 0;   // Reset the verification counter
        }
        // Reset attempts
        setRemainingAttempts(3);
    }
}
