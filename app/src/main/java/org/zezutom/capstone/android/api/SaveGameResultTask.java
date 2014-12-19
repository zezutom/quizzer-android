package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.GameResult;

public class SaveGameResultTask extends BaseApiTask<GameResult> {

    private int score;

    private int round;

    private int powerUps;

    private int oneTimeAttempts;

    private int twoTimeAttempts;

    public SaveGameResultTask(Activity activity, Quizzer.QuizzerApi api,
                              ResponseListener<GameResult> listener) {
        super(activity, api, listener);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setPowerUps(int powerUps) {
        this.powerUps = powerUps;
    }

    public void setOneTimeAttempts(int oneTimeAttempts) {
        this.oneTimeAttempts = oneTimeAttempts;
    }

    public void setTwoTimeAttempts(int twoTimeAttempts) {
        this.twoTimeAttempts = twoTimeAttempts;
    }

    @Override
    protected GameResult execute() throws IOException {
        return api.saveGameResult(round, score, powerUps, oneTimeAttempts, twoTimeAttempts).execute();
    }
}
