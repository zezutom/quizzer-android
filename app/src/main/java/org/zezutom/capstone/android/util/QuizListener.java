package org.zezutom.capstone.android.util;

import java.util.List;

import zezutom.org.quizService.model.Quiz;
import zezutom.org.quizService.model.QuizRating;

public interface QuizListener {

    void onGetAllSuccess(List<Quiz> quizzes);

    void onGetAllError(Exception ex);

    void onRateSuccess(QuizRating quizRating);

    void onRateError(Exception ex);
}
