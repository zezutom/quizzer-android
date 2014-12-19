package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;
import java.util.List;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.Quiz;

public class GetQuizzesTask extends BaseApiTask<List<Quiz>> {

    public GetQuizzesTask(Activity activity,
                          ResponseListener<List<Quiz>> listener) {
        super(activity, listener);
    }

    @Override
    protected List<Quiz> execute(Quizzer.QuizzerApi api) throws IOException {
        return api.getQuizzes().execute().getItems();
    }
}
