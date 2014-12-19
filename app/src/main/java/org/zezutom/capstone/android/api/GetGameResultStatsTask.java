package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.GameResultStats;

public class GetGameResultStatsTask extends BaseApiTask<GameResultStats> {

    public GetGameResultStatsTask(Activity activity,
                                  ResponseListener<GameResultStats> listener) {
        super(activity, listener);
    }

    @Override
    protected GameResultStats execute(Quizzer.QuizzerApi api) throws IOException {
        return api.getGameResultStats(getEmail()).execute();
    }
}
