package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;
import java.util.List;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.Quiz;

public class GetQuizzesTask extends BaseApiTask<List<Quiz>> {

    public GetQuizzesTask(Activity activity, Quizzer.QuizzerApi api,
                          ResponseListener<List<Quiz>> listener) {
        super(activity, api, listener);
    }

    @Override
    protected List<Quiz> execute() throws IOException {
        return api.getQuizzes().execute().getItems();
    }
}
