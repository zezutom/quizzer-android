package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.GameResultStats;

public class GetGameResultStatsTask extends BaseApiTask<GameResultStats> {

    public GetGameResultStatsTask(Activity activity, Quizzer.QuizzerApi api,
                                  ResponseListener<GameResultStats> listener) {
        super(activity, api, listener);
    }

    @Override
    protected GameResultStats execute() throws IOException {
        return api.getGameResultStats().execute();
    }
}
