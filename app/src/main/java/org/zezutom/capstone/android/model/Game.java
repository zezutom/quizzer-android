package org.zezutom.capstone.android.model;

public class Game {

    private int round = 1;

    private int score;

    private int powerUps;

    private int oneTimeCorrectAttempts;

    private int remainingAttempts = 3;

    private boolean gameOver;

    private GameHistory history;

    public Game() {
        this.history = new GameHistory();
    }

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

    public int getOneTimeCorrectAttempts() {
        return oneTimeCorrectAttempts;
    }

    public void setOneTimeCorrectAttempts(int oneTimeCorrectAttempts) {
        this.oneTimeCorrectAttempts = oneTimeCorrectAttempts;
    }

    public GameHistory getHistory() {
        return history;
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

    public boolean isGameInProgress() {
        return !isGameOver() && (
                score > 0 || powerUps > 0 || oneTimeCorrectAttempts > 0 || remainingAttempts < 3);
    }

    public int nextRound() {
        return ++round;
    }

    public void score() {
        switch (remainingAttempts) {
            case 3:
                score += 5;
                oneTimeCorrectAttempts++;
                history.incrementOneTimeAttempts();
                break;
            case 2:
                score += 3;
                history.incrementTwoTimeAttempts();
                break;
            case 1:
                score++;
                break;
            default:
                gameOver = true;
        }

        // Increase power ups if there have been at least two consecutive correct attempts
        if (oneTimeCorrectAttempts > 1) {
            powerUps++;
            history.incrementPowerUps();
            oneTimeCorrectAttempts = 0;   // Reset the verification counter
        }
        // Reset attempts
        setRemainingAttempts(3);
    }
}
