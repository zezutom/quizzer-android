package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;
import java.util.List;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.GameResult;
import zezutom.org.quizzer.model.GameResultStats;

public class GetGameResultsTask extends BaseApiTask<List<GameResult>> {

    private String email;

    public GetGameResultsTask(Activity activity,
                              ResponseListener<List<GameResult>> listener) {
        super(activity, listener);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    protected List<GameResult> execute(Quizzer.QuizzerApi api) throws IOException {
        return api.getGameResults(email).execute().getItems();
    }
}
