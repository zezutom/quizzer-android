package org.zezutom.capstone.android.api;

import android.app.Activity;

import java.io.IOException;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.QuizRating;

public class RateQuizTask extends BaseApiTask<QuizRating> {

    private String quizId;

    private boolean liked;

    public RateQuizTask(Activity activity,
                        ResponseListener<QuizRating> listener) {
        super(activity, listener);
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    protected QuizRating execute(Quizzer.QuizzerApi api) throws IOException {
        return api.rateQuiz(getEmail(), liked, quizId).execute();
    }
}
